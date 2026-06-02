# Secluso OS

A verifiable OS to accompany our core software. 

The benefits of this OS over Raspberry Pi OS Lite are:
- We use a (read-only, compressed) squashfs filesystem for the rootfs. This is not in the latest release, but it will be out soon. 
- We have reproducible builds that have our camera firmware baked in.
- We have stripped out some dependencies such as the package manager that don't need to be included in a project such as this.

There is substantial work left to complete the long-term goals of this repository. Notably, some things to-do are regular OS updates to patch CVEs via A/B, hardening the kernel, continue stripping out un-used parts of the OS, etc. 

### Check at any time that your SD card is running official Secluso OS (not in latest release)

We provide an easy way to check that your SD card is running an official Secluso OS image!

**This was made for macOS and Linux**. On Windows, you would need to find some way to install dd and hash the output test.bin. Windows has "cygwin" to help with this.

1. Take out your SD card
2. Plug it into your laptop or computer
3. Determine the device identifier (we recommend using https://github.com/Canop/dysk) - you should see a disk that is labeled as "removable" and has at least two partitions visible, "boot" and "provision". Look at the number that comes **right after** "disk". You'll need to use this in the next step.
4. Run the command sudo dd if=/dev/diskNUMBERs2 of=test.bin 
   5. For example, if your number from Step #3 was 6, then you would run sudo dd if=/dev/disk6s2 of=test.bin
5. Run sha256sum test.bin
6. Compare the output to our released squashfs file in this repository's releases. 

### Check that our release matches a local build (reproducible builds)

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
