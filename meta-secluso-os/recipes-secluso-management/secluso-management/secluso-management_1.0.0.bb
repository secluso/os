# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

SUMMARY = "Version number of Secluso OS"
LICENSE = "GPL-3.0-or-later"

PV = "1.0.1"

do_install() {
    mkdir -p ${D}${sysconfdir}
    echo "${PV}" > ${D}${sysconfdir}/secluso-os-version
}

