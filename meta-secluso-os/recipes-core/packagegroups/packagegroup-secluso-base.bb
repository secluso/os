# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

DESCRIPTION = "Secluso Base Package Group"

# Pulls in Yocto's packagegroup class
# "the packagegroup class sets default values appropriate for package group recipes (e.g. PACKAGES, PACKAGE_ARCH, ALLOW_EMPTY, and so forth). It is highly recommended that all package group recipes inherit this class."
# See https://docs.yoctoproject.org/ref-manual/classes.html#packagegroup for ref
inherit packagegroup

# ${PN} is a placeholder for the package name of the current recipe
# "Lists runtime dependencies of a package. These dependencies are other packages that must be installed in order for the package to function correctly."
# See https://docs.yoctoproject.org/ref-manual/variables.html#rdepends for ref
#
# We include our custom rpicam-apps-fork (see recipes-rpicam for more info)
RDEPENDS:${PN} = " \
    rpicam-apps-fork \
    secluso-camera-hub \
"