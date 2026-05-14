# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# Regardless of us creating the partition in the .wks file, it does not automatically mount it
# We can make this happen by appending to fstab [https://man7.org/linux/man-pages/man5/fstab.5.html]
do_install:append() {
    # Create mount points
    install -d ${D}/provision
    install -d ${D}/data

    # nofail = "do not report errors for this device if it does not exist."
    #
    # /data passno=2 means systemd-fsck@dev-mmcblk0p4.service runs e2fsck at every boot, which catches and repairs any inconsistencies left by the first-boot resize2fs in grow-data-partition.sh.
    echo "LABEL=provision  /provision  vfat  defaults,nofail        0  0"  >> ${D}${sysconfdir}/fstab
    echo "LABEL=data       /data       ext4  defaults,nofail        0  2"  >> ${D}${sysconfdir}/fstab
}
