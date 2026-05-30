# Secluso OS

A verifiable OS to accompany our core software. 

The benefits of this OS over Raspberry Pi OS Lite are:
- We use a (read-only, compressed) squashfs filesystem for the rootfs. This is not in the latest release, but it will be out soon. 
- We have reproducible builds that have our camera firmware baked in.
- We have stripped out some dependencies such as the package manager that don't need to be included in a project such as this.

There is substantial work left to complete the long-term goals of this repository. Notably, some things to-do are regular OS updates to patch CVEs via A/B, hardening the kernel, continue stripping out un-used parts of the OS, etc. 


Please see below on how to check reproducible builds.

To build, run this command on x86-64 Linux from the meta-secluso-os directory:
```chatinput
kas build pi-official-image.yml
```

The image can be found after building running this from the meta-secluso-os directory:
```chatinput
cd build/tmp/deploy/images/raspberrypi0-2w-64
bunzip2 -f secluso-pi-image-minimal-raspberrypi0-2w-64.rootfs.wic.bz2
```

**To check that our released image is reproducible**, obtain a fresh image from above, and then compare to our released image file:
```chatinput
diff (our released image.wic) secluso-pi-image-minimal-raspberrypi0-2w-64.rootfs.wic
```

If they don't match, please make sure you've checked out the tag corresponding to the release.

This repository is licensed under GPL-3.0. See LICENSE, COPYRIGHT, and
NOTICE for the applicable copyright and additional notice-preservation terms.
