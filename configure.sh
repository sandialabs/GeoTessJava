#!/bin/bash

# This script creates bash scripts in the current directory
# called 'geotess' and 'geotessbuilder'

# find the full path to the geo-tess-java jar file located in this directory
jarfile=$(pwd)/target/$(basename $(pwd)/$(find . -name '*-jar-with-dependencies.jar'))

echo jarfile is "$jarfile"

if [[ -z "$jarfile" ]]; then
	echo ERROR: geotess jar file not found in $(pwd)
	exit -1
fi

# ---- GeoTessExplorer
echo "Creating executable script file geotess that launches GeoTessExplorer"
echo "#!/bin/bash" > geotess
echo "java -jar $jarfile \$*" >> geotess

chmod 777 geotess

# ---- 
echo "Creating executable script file geotessbuilder that launches GeoTessBuilderMain"
echo "#!/bin/bash" > geotessbuilder
echo "java -cp $jarfile gov.sandia.geotessbuilder.GeoTessBuilderMain \$*" >> geotessbuilder

chmod 777 geotessbuilder

# ---- Add to path
# The script also prints to screen recommended addition to 
# the user's .cshrc, .bash_profile, or .profile that will make the new
# executable available via the PATH.  No changes to the user's
# environment are actually made.

if [ `uname -s` = Darwin ]; then
	echo "Add this line to your .bash_profile"
	echo "export PATH=$(pwd):\$PATH"
else
	echo "Add this line to your .cshrc file"
	echo "set path=( $(pwd) \$path )"
fi
