# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://disable-ikconfig.cfg"
