SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"

ALLOW_EMPTY:${PN} = "1"

python do_display_banner() {
    bb.plain("***********************************************");
    bb.plain("*                                             *");
    bb.plain("*  Example recipe created by bitbake-layers   *");
    bb.plain("*                                             *");
    bb.plain("***********************************************");
}

addtask display_banner before do_build