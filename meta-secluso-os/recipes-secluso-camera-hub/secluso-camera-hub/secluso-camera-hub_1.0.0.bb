# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

#TODO: Verify reproducible

# Referenced https://docs.yoctoproject.org/dev-manual/new-recipe.html
# Referenced https://github.com/facebook/openbmc/blob/5bf12b96fd7797c84798049bc404581be8390bc7/meta-facebook/meta-catalina/recipes-catalina/satellite-relay/satellite-relay_0.1.0.bb

# Inheriting cargo is what will build the crate here.
inherit cargo cargo-update-recipe-crates systemd

SUMMARY = "Recipe to build and install Secluso Camera Hub"
HOMEPAGE = "https://github.com/secluo/core"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b769fddc23425484f6d001e49426c2ee"

# This is our own repository (set to an immutable commit)
SRC_URI = "git://github.com/secluso/core.git;branch=remove-openssl-from-camera-hub;protocol=https"
SRCREV = "773c4f4c5f90febd5ef4ef1ffe3d586da0f0d5e8"

# Cargo fingerprints local path crates using their absolute source path
# Thus, we copy the workspace to a canonical location before compiling.
REPRODUCIBLE_SOURCE_DIR = "/tmp/yocto-reproducible-sources/${BPN}-${PV}-${TARGET_SYS}"
S = "${REPRODUCIBLE_SOURCE_DIR}"

# onnxruntime adds 314 MB to the image size
# ncmli adds in 90 MB to the image size. TODO: I think we can do it without by using existing packages
RDEPENDS:${PN} += " networkmanager networkmanager-nmcli onnxruntime"
# In meta-rust, it shows we can override CARGO_SRC_DIR to specify our intended source directory within repository [https://github.com/meta-rust/meta-rust/blob/master/classes/cargo.bbclass]
CARGO_SRC_DIR = "camera_hub"
CARGO_BUILD_FLAGS += "--features raspberry"

# The binary gets installed in /usr/bin/secluso-camera-hub per https://github.com/meta-rust/meta-rust/blob/328334d9d31241d1d29eb754c5c102d5b0e002ab/classes/rust-bin.bbclass#L5

# rustc documents --remap-path-prefix as the supported way to rewrite build paths in emitted diagnostics, debug info, and macro expansions.
# mirror trick above with path remaps so the binary doesn't keep references to ${WORKDIR}, ${S}, or the builder's cargo home
# See https://doc.rust-lang.org/rustc/remap-source-paths.html
RUSTFLAGS += " --remap-path-prefix=${WORKDIR}=/usr/src/debug/${PN}/${PV}"
RUSTFLAGS += " --remap-path-prefix=${S}=/usr/src/debug/${PN}/${PV}/sources"
RUSTFLAGS += " --remap-path-prefix=${CARGO_HOME}=cargo_home"

# https://wiki.koansoftware.com/index.php/Add_a_systemd_service_file_into_a_Yocto_image
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "secluso_camera_hub.service"
SRC_URI:append = " file://secluso_camera_hub.service "
FILES:${PN} += "${systemd_unitdir}/system/secluso_camera_hub.service"
RDEPENDS:${PN} += " systemd"

python do_unpack:append() {
    import os
    import shutil

    source_dir = os.path.join(d.getVar("WORKDIR"), "sources", f"{d.getVar('BPN')}-{d.getVar('PV')}")
    reproducible_source_dir = d.getVar("S")

    # Recreate the canonical source tree on every unpack
    bb.utils.remove(reproducible_source_dir, recurse=True)
    bb.utils.mkdirhier(os.path.dirname(reproducible_source_dir))
    shutil.copytree(source_dir, reproducible_source_dir, symlinks=True)
}

# inherit cargo has its own do_install that installs the secluso_camera_hub binary into /usr/bin. thus, we append
do_install:append() {
    mkdir -p ${D}${localstatedir}/lib/secluso
    install -d ${D}/${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/secluso_camera_hub.service ${D}/${systemd_unitdir}/system
}

# https://docs.yoctoproject.org/dev/ref-manual/classes.html#cargo-update-recipe-crates
# Generate new one: `bitbake -c update_crates secluso-camera-hub` from project root
require ${BPN}-crates.inc
