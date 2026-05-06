# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

SUMMARY = "Secluso Minimal Raspberry Pi Image"
LICENSE = "GPL-3.0"

# Pull in the existing minimal image to build off of
# core-image-minimal: https://github.com/openembedded/openembedded-core/blob/master/meta/recipes-core/images/core-image-minimal.bb
require recipes-core/images/core-image-minimal.bb

# Include packagegroups/packagegroup-secluso-base.bb package bundle to add to the image's package's install list
# See https://docs.yoctoproject.org/ref-manual/variables.html#term-IMAGE_INSTALL for ref
IMAGE_INSTALL:append = " packagegroup-secluso-base"

# NetworkManager manages dnsmasq as a child process. 
# Remove the systemd wants symlink so dnsmasq does not start as a standalone service.
# Must run after systemd_handle_machine_id which recreates the symlink.
ROOTFS_POSTPROCESS_COMMAND:append = " disable_dnsmasq_service;"

disable_dnsmasq_service() {
    rm -f ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/dnsmasq.service
}
