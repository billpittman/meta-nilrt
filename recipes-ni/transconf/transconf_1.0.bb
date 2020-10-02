SUMMARY = "Configuration transcoder utility"
DESCRIPTION = " \
    A utility to save and restore system configurations across \
    format/imaging operations. \
"
SECTION = "base"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

inherit allarch ptest

PACKAGES =+ "${PN}-example"

DEPENDS += "bash"

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-example += "${PN} bash"
RDEPENDS_${PN}-ptest += "${PN} ${PN}-example bash"

FILES_${PN}-example = "${sysconfdir}/transconf/hooks/example"

S = "${WORKDIR}"

SRC_URI = " \
    file://transconf \
    file://example_transconf_hook \
    file://run-ptest \
    file://transconf-migrate-init \
"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/transconf ${D}${sbindir}/

    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/transconf
    install -d ${D}${sysconfdir}/transconf/hooks
    install -m 0755 ${S}/example_transconf_hook ${D}${sysconfdir}/transconf/hooks/example

    install -d ${D}${sysconfdir}/rcS.d/
    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${S}/transconf-migrate-init ${D}${sysconfdir}/init.d/
    ln -s ../init.d/transconf-migrate-init ${D}${sysconfdir}/rcS.d/S29transconf-migrate-init
}

do_install_ptest_append () {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${S}/run-ptest ${D}${PTEST_PATH}/
}

# XXX OE can't sub-package "${sysconfdir}/transconf/hooks/example" file
#  then package an empty "${sysconfdir}/transconf/hooks/" directory.
#  Workaround in postinst script.
pkg_postinst_${PN} () {
    [ -e "$D${sysconfdir}/transconf" ] || mkdir -m 0755 "$D${sysconfdir}/transconf"
    [ -e "$D${sysconfdir}/transconf/hooks" ] || mkdir -m 0755 "$D${sysconfdir}/transconf/hooks"
}

# XXX Has dependents which are native and/or nativesdk
BBCLASSEXTEND += "native nativesdk"
