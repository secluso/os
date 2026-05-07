# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://managed.conf"

PACKAGECONFIG:append = " dnsmasq"
RDEPENDS:${PN} += "dnsmasq"

# By default, wifi is considered an UNmanaged device, and thus NetworkManager cannot configure it. We fix that here
do_install:append() {
    install -d ${D}${sysconfdir}/NetworkManager/conf.d
    install -m 0644 ${UNPACKDIR}/managed.conf \
        ${D}${sysconfdir}/NetworkManager/conf.d/managed.conf
}

FILES:${PN} += "${sysconfdir}/NetworkManager/conf.d/managed.conf"
