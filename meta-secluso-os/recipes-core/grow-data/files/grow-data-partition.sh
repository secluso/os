#!/bin/sh
# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

set -e

DISK=/dev/mmcblk0

# /data is partition 4
PART_NUM=4
PART_DEV=${DISK}p${PART_NUM}

if [ -f /var/lib/grow-data-done ]; then
    exit 0
fi

echo "Growing /data partition to fill remaining SD card space..."

# Tell the kernel to re-read the partiion table first
partprobe ${DISK} 2>/dev/null || true

# Resize partition 4 to use all remaining space
parted -s ${DISK} resizepart ${PART_NUM} 100%

# Inform kernel of the change and wait for udev to finish recreating the device node before touching it (a fixed sleep races with node recreation)
partprobe ${DISK}
udevadm settle

# resize2fs needs a clean filesystem; e2fsck exit codes 0 and 1 are both OK (1 = errors found and corrected)
# also matches the fsck-every-boot intent for /data and avoids racing systemd-fsck@dev-mmcblk0p4.service.
e2fsck -fp ${PART_DEV} || [ $? -le 1 ]

# Grow the ext4 filesystem to fill the new partition size
resize2fs ${PART_DEV}

# Mark as done so we never run again
mkdir -p /var/lib
touch /var/lib/grow-data-done

echo "Done. /data is now full SD card size."