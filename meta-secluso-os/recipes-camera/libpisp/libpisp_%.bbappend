# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# See the patch file for more information.
SRC_URI += "file://0001-src-meson-drop-build_rpath.patch"