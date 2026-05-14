# Secluso OS

A verifiable OS to accompany our core software. 

There is substantial work left to complete the long-term goals of this repository. Notably, some things to-do are making rootfs read-only, regular OS updates to patch CVEs via A/B, hardening the kernel, continue stripping out un-used parts of the OS, etc. Currently, it is considered to be slightly better in security than Raspberry Pi's distributed headless image, with the addition of our software stack and full byte-for-byte reproducibility of the entire image.


Please see below on how to check reproducible builds.

To build, run this command on x86-64 Linux from the meta-secluso-os directory:
```chatinput
kas build build-pi-image.yml
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
