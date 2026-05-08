# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# GCC documents -fdebug-prefix-map as a reproducible-build tool for rewriting build-time paths in debug info
# Source: https://gcc.gnu.org/onlinedocs/gcc/Debugging-Options.html
#
# GCC documents -gno-record-gcc-switches as disabling the DW_AT_producer command-line recording that is enabled by default.
OECMAKE_C_FLAGS += " -ffile-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -fmacro-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -gno-record-gcc-switches"
OECMAKE_CXX_FLAGS += " -ffile-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -fmacro-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV} -gno-record-gcc-switches"

# GCC 15 started flagging a small number of warnings in upstream onnxruntime/pybind11 code under libcamera's global -Werror
OECMAKE_CXX_FLAGS += " -Wno-error=maybe-uninitialized -Wno-error=uninitialized -Wno-error=array-bounds -Wno-error=deprecated-enum-enum-conversion -Wno-error=free-nonheap-object"

# The base recipe hardcodes CMAKE_CXX_FLAGS in EXTRA_OECMAKE, which overrides OECMAKE_CXX_FLAGS and drops our prefix-map flags
EXTRA_OECMAKE:remove = " \
    -DCMAKE_CXX_FLAGS=-Wno-error=maybe-uninitialize \
    -DCMAKE_CXX_FLAGS=-Wno-error=array-bounds \
    -DCMAKE_CXX_FLAGS=-Wno-error=deprecated-enum-enum-conversion \
"

do_install:append() {
    # PEP 610 (https://peps.python.org/pep-0610/) says direct_url.json has installers to record the origin URL of installed distributions.
    # In here it captures the local wheel/build path
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/onnxruntime-${DPV}.dist-info/direct_url.json

    # RECORD hashes direct_url.json / etc therefore it needs to be removed too
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/onnxruntime-${DPV}.dist-info/RECORD

    # Avoid nondeterministic target image bytecode
    find ${D}${PYTHON_SITEPACKAGES_DIR}/onnxruntime -name '__pycache__' -type d -prune -exec rm -rf {} +
}