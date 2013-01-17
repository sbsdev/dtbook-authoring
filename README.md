# dtbook-authoring: DTBook Authoring Mode for Oxygen

## Building

    mvn clean package

## Hacking

Developer mode:

    oxygen.install.dir=/home/frees/.eclipse/org.eclipse.platform_3.7.0_1473617060/plugins/com.oxygenxml.editor_13.2.0.v2012030716
    ln -s target/framework ${oxygen.install.dir}/frameworks/dtbook

## Authors

* Bert Frees <bert.frees@sbs.ch>

## License

Copyright 2013 Swiss Library for the Blind, Visual Impaired and Print Disabled.

Licensed under GNU Lesser General Public License as published by the Free Software Foundation,
either [version 3](http://www.gnu.org/licenses/gpl-3.0.html) of the License, or (at your option) any later version.
