# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# Note: See secluso-camera-hub_1.0.0.bb for references/notes on this file.

inherit cargo cargo-update-recipe-crates systemd

SUMMARY = "Recipe to build and install the Secluso OS updater"
HOMEPAGE = "https://github.com/secluso/core"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b769fddc23425484f6d001e49426c2ee"

SRC_URI = " \
    git://github.com/secluso/core.git;branch=main;protocol=https \
    file://secluso_update.service \
"
SRCREV = "230d2b2568fdfd503e21d15cccfa89729456450f"

REPRODUCIBLE_SOURCE_DIR = "/tmp/yocto-reproducible-sources/${BPN}-${PV}-${TARGET_SYS}"
S = "${REPRODUCIBLE_SOURCE_DIR}"

CARGO_SRC_DIR = "update"

RUSTFLAGS += " --remap-path-prefix=${WORKDIR}=/usr/src/debug/${PN}/${PV}"
RUSTFLAGS += " --remap-path-prefix=${S}=/usr/src/debug/${PN}/${PV}/sources"
RUSTFLAGS += " --remap-path-prefix=${CARGO_HOME}=cargo_home"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "secluso_update.service"

RDEPENDS:${PN} += " \
    ca-certificates \
    secluso-camera-hub \
    systemd \
"

FILES:${PN} += " \
    ${systemd_unitdir}/system/secluso_update.service \
"

python do_unpack:append() {
    import os
    import shutil

    source_dir = os.path.join(d.getVar("WORKDIR"), "sources", f"{d.getVar('BPN')}-{d.getVar('PV')}")
    reproducible_source_dir = d.getVar("S")

    bb.utils.remove(reproducible_source_dir, recurse=True)
    bb.utils.mkdirhier(os.path.dirname(reproducible_source_dir))
    shutil.copytree(source_dir, reproducible_source_dir, symlinks=True)
}

do_install:append() {
    install -d ${D}${localstatedir}/lib/secluso
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/secluso_update.service ${D}${systemd_unitdir}/system
}

# Regenerate after dependency changes with:
# Generate new one: `bitbake -c update_crates secluso-update` from project root
require ${BPN}-crates.inc

