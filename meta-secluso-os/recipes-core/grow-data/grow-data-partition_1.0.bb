# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

SUMMARY = "Grow /data partition to full SD card size on first boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://grow-data-partition.sh \
    file://grow-data-partition.service \
"

# See secluso_camera_hub recipe for discussion on the layout of this file 

REPRODUCIBLE_SOURCE_DIR = "/tmp/yocto-reproducible-sources/${BPN}-${PV}-${TARGET_SYS}"
S = "${REPRODUCIBLE_SOURCE_DIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "grow-data-partition.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} = "parted e2fsprogs-resize2fs e2fsprogs-e2fsck"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/grow-data-partition.sh ${D}${sbindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/grow-data-partition.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}/grow-data-partition.service"