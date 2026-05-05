# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# Regardless of us creating the partition in the .wks file, it does not automatically mount it
# We can make this happen by appending to fstab [https://man7.org/linux/man-pages/man5/fstab.5.html]
do_install:append() {
    # Create mount point
    install -d ${D}/provision

    # Append provision partition entry to /etc/fstab (sysconfdir = /etc)
    # UUID taken from the custom wks/sdcard-raspberrypi.wks we set earlier
    # nofail = "do not report errors for this device if it does not exist."
    echo "UUID=131c42eb-25a8-4179-8498-0d5ddbda2661  /provision  vfat  defaults,nofail  0  0"  >> ${D}${sysconfdir}/fstab
}
