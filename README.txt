=========================== How to use ===================================

Find the ready to use SWC and ANE inside the /build folder.
Still under development

=========================== What's inside ================================

The /as folder contains a FlashDevelop project for developing the action script part of the extension and may be creating a testbench (in the future).
The /java folder contains a ADT (Eclipse) project with the native java source code.
The /platform folder conatins all the files that are needed to package the extension.

=========================== Building =====================================

To build the stuff yourself using the current setup you need: ADT, FlashDevelop and Perl 
1) Open the Eclipse project and export the jar file into the /platfrom/Android folder
2) Building the SWC and the ANE is automatic and is configured as a post-build instruction of the FlashDevelop project. It is done by a perl script - as/package_extension.pl. You just run the build command in FlashDevelop (F8).