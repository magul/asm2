#!/bin/sh

# Attempts to find dodgy i18n keys (ones that start with a space
# or bracket)
cd `dirname $0`/../src/locale
grep '^\_' *.properties
grep '^\(' *.properties

