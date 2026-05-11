#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This implements the 'bootimg_partition' source plugin class for
# 'wic'. The plugin creates an image of boot partition, copying over
# files listed in IMAGE_BOOT_FILES bitbake variable.
#
# AUTHORS
# Maciej Borzecki <maciej.borzecki (at] open-rnd.pl>
#

# MODIFIED AREA 1: AUDIT NOTE.
# This file is an exact copy of openembedded-core tag yocto-5.3.3 except for blocks explicitly marked "MODIFIED AREA".
# TODO: Before updating Yocto version in the future, check for modifications.
#
# Upstream source:
#   openembedded-core commit: ab57471acad7ce2a037480dc7b301104620f1ebf
#   scripts/lib/wic/plugins/source/bootimg_partition.py
#   upstream sha256: c1df6d0d1f6e456254a9abad4e07a400dedc476a4ff54522bc36ca2bab741ee7
# END MODIFIED AREA 1.

import logging
import os
import re

from oe.bootfiles import get_boot_files

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, get_bitbake_var

logger = logging.getLogger('wic')

class BootimgPartitionPlugin(SourcePlugin):
    """
    Create an image of boot partition, copying over files
    listed in IMAGE_BOOT_FILES bitbake variable.
    """

    # MODIFIED AREA 2: PLUGIN REGISTRATION NAME.
    # Keep OE-Core's class implementation but register it under a project-local source name so the WKS file can opt into the deterministic variant
    name = 'secluso_bootimg_partition'
    # END MODIFIED AREA 2.
    image_boot_files_var_name = 'IMAGE_BOOT_FILES'

    @classmethod
    def do_configure_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             native_sysroot):
        """
        Called before do_prepare_partition(), create u-boot specific boot config
        """
        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)
        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        boot_files = None
        for (fmt, id) in (("_uuid-%s", part.uuid), ("_label-%s", part.label), (None, None)):
            if fmt:
                var = fmt % id
            else:
                var = ""

            boot_files = get_bitbake_var(cls.image_boot_files_var_name + var)
            if boot_files is not None:
                break

        if boot_files is None:
            raise WicError('No boot files defined, %s unset for entry #%d' % (cls.image_boot_files_var_name, part.lineno))

        logger.debug('Boot files: %s', boot_files)

        cls.install_task = get_boot_files(kernel_dir, boot_files)
        # MODIFIED AREA 3: DETERMINISTIC INSTALL ORDER.
        # preserves IMAGE_BOOT_FILES/glob order here.
        # (sort by final destination then source before staging so FAT cluster allocation is maintained across build hosts)
        cls.install_task = sorted(cls.install_task, key=lambda task: (task[1], task[0]))
        # END MODIFIED AREA 3.
        if source_params.get('loader') != "u-boot":
            return

        configfile = cr.ks.bootloader.configfile
        custom_cfg = None
        if configfile:
            custom_cfg = get_custom_config(configfile)
            if custom_cfg:
                # Use a custom configuration for extlinux.conf
                extlinux_conf = custom_cfg
                logger.debug("Using custom configuration file "
                             "%s for extlinux.conf", configfile)
            else:
                raise WicError("configfile is specified but failed to "
                               "get it from %s." % configfile)

        if not custom_cfg:
            # The kernel types supported by the sysboot of u-boot
            kernel_types = ["zImage", "Image", "fitImage", "uImage", "vmlinux"]
            has_dtb = False
            fdt_dir = '/'
            kernel_name = None

            # Find the kernel image name, from the highest precedence to lowest
            for image in kernel_types:
                for task in cls.install_task:
                    src, dst = task
                    if re.match(image, src):
                        kernel_name = os.path.join('/', dst)
                        break
                if kernel_name:
                    break

            for task in cls.install_task:
                src, dst = task
                # We suppose that all the dtb are in the same directory
                if re.search(r'\.dtb', src) and fdt_dir == '/':
                    has_dtb = True
                    fdt_dir = os.path.join(fdt_dir, os.path.dirname(dst))
                    break

            if not kernel_name:
                raise WicError('No kernel file found')

            # Compose the extlinux.conf
            extlinux_conf = "default Yocto\n"
            extlinux_conf += "label Yocto\n"
            extlinux_conf += "   kernel %s\n" % kernel_name
            if has_dtb:
                extlinux_conf += "   fdtdir %s\n" % fdt_dir
            bootloader = cr.ks.bootloader
            extlinux_conf += "append root=%s rootwait %s\n" \
                             % (cr.rootdev, bootloader.append if bootloader.append else '')

        install_cmd = "install -d %s/extlinux/" % hdddir
        exec_cmd(install_cmd)
        cfg = open("%s/extlinux/extlinux.conf" % hdddir, "w")
        cfg.write(extlinux_conf)
        cfg.close()


    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        In this case, does the following:
        - sets up a vfat partition
        - copies all files listed in IMAGE_BOOT_FILES variable
        """
        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)

        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        logger.debug('Kernel dir: %s', bootimg_dir)


        for task in cls.install_task:
            src_path, dst_path = task
            logger.debug('Install %s as %s', src_path, dst_path)
            install_cmd = "install -m 0644 -D %s %s" \
                          % (os.path.join(kernel_dir, src_path),
                             os.path.join(hdddir, dst_path))
            exec_cmd(install_cmd)

        logger.debug('Prepare boot partition using rootfs in %s', hdddir)
        # MODIFIED AREA 4: DETERMINISTIC FAT IMAGE POPULATION.
        # OE-Core calls part.prepare_rootfs whose vfat path eventually runs "mcopy -s <staging-dir>/* ::/"
        # That recursive copy lets mtools observe host filesystem directory order, which made OVERLAYS/*.dtbo  cluster placement differ across build hosts
        # Create the FAT image here and copy staged files one-by-one in sorted (destination) order
        from wic.misc import exec_native_cmd

        rootfs = "%s/rootfs_%s.%s.%s" % (cr_workdir, part.label,
                                         part.lineno, part.fstype)
        if os.path.isfile(rootfs):
            os.remove(rootfs)

        du_cmd = "du -bks %s" % hdddir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])
        rootfs_size = part.get_rootfs_size(blocks)

        label_str = "-n boot"
        if part.label:
            label_str = "-n %s" % part.label

        extraopts = part.mkfs_extraopts or '-S 512'
        dosfs_cmd = "mkdosfs %s -i %s %s -C %s %d" % \
                    (label_str, part.fsuuid, extraopts, rootfs,
                     rootfs_size)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        copy_tasks = []
        for dirpath, dirs, files in os.walk(hdddir):
            dirs.sort()
            files.sort()
            for filename in files:
                src_path = os.path.join(dirpath, filename)
                dst_path = os.path.relpath(src_path, hdddir)
                copy_tasks.append((dst_path, src_path))

        created_dirs = set()
        for dst_path, src_path in sorted(copy_tasks):
            dst_dir = os.path.dirname(dst_path)
            if dst_dir:
                current_dir = ""
                for component in dst_dir.split(os.sep):
                    if not component:
                        continue
                    current_dir = component if not current_dir else \
                                  os.path.join(current_dir, component)
                    if current_dir not in created_dirs:
                        mmd_cmd = "mmd -i %s ::/%s" % (rootfs, current_dir)
                        exec_native_cmd(mmd_cmd, native_sysroot)
                        created_dirs.add(current_dir)

            mcopy_cmd = "mcopy -i %s %s ::/%s" % (rootfs, src_path, dst_path)
            exec_native_cmd(mcopy_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % rootfs
        exec_cmd(chmod_cmd)
        part.source_file = rootfs

        du_cmd = "du --apparent-size -Lks %s" % rootfs
        out = exec_cmd(du_cmd)
        part.size = int(out.split()[0])
        # END MODIFIED AREA 4.
