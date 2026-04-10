# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

#TODO: Verify reproducible
#TODO: Install in correct path
#TODO: Setup system services

# Referenced https://docs.yoctoproject.org/dev-manual/new-recipe.html
# Referenced https://github.com/facebook/openbmc/blob/5bf12b96fd7797c84798049bc404581be8390bc7/meta-facebook/meta-catalina/recipes-catalina/satellite-relay/satellite-relay_0.1.0.bb

# Inheriting cargo is what will build the crate here.
inherit cargo cargo-update-recipe-crates

SUMMARY = "Recipe to build and install Secluso Camera Hub"
HOMEPAGE = "https://github.com/secluo/core"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b769fddc23425484f6d001e49426c2ee"

# This is our own repository (set to an immutable commit)
SRC_URI = "git://github.com/secluso/core.git;branch=main;protocol=https"
SRCREV = "dc2a3d4fbc48b29641402476c17a9824b92bf019"

# In meta-rust, it shows we can override CARGO_SRC_DIR to specify our intended source directory within repository [https://github.com/meta-rust/meta-rust/blob/master/classes/cargo.bbclass]
CARGO_SRC_DIR = "camera_hub"

# https://docs.yoctoproject.org/dev/ref-manual/classes.html#cargo-update-recipe-crates
# Generate new one: `bitbake -c update_crates secluso-camera-hub` from project root
require ${BPN}-crates.inc
