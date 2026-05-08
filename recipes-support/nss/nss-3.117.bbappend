# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# creates placeholder .chk files during do_install and then regenerates them in pkg_postinst with shlibsign
# shlibsign is nondeterministic even on identical input libraries.
pkg_postinst:${PN}() {
        :
}