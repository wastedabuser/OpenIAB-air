use strict;

my ($projectPath, $name) = @ARGV;

die "build_config.pl not found" unless -f "build_config.pl";
my ($flexSdkPath, $zipPath7z, $productionPath) = do "build_config.pl"; 

die "Sdk path is not configured!" unless $flexSdkPath;
die "7 zip path is not configured!" unless $zipPath7z;

my $swc = "$projectPath\\..\\build\\$name.swc";
my $ane = "$projectPath\\..\\build\\com.eldhelm.openiab.InAppPurchase.ane";

print "Cleaning up build directory\n";
unlink $swc;
unlink $ane;

opendir DIR, "../platform";
my @platfroms = grep /^[^\.]/, readdir DIR;
close DIR;

my @commands = (

	qq~"$flexSdkPath/bin/acompc"
		-source-path src
		-namespace com.eldhelm.openiab extensionManifest.xml
		-include-namespaces com.eldhelm.openiab
		-swf-version=23
		-output $swc
	~,

	map (qq~"$zipPath7z/7z" 
		e 
		$swc
		library.swf
		-o"../platform/$_"
		-y
	~, @platfroms),

	qq~"$flexSdkPath/bin/adt" 
		-package
		-target ane $ane extension.xml 
		-swc $swc
	~.join(" ", map qq~-platform $_ ~.(-f "platform$_.xml" ? "-platformoptions platform$_.xml ":"").qq~-C ../platform/$_ .~, @platfroms),

	-d $productionPath ? (
		# qq~copy "$swc" "$productionPath/lib" /y~,
		qq~copy "$ane" "$productionPath/ane" /y~,
	) : ()
);

foreach (@commands) {
	print "$_\n";
	s/[\n\r]//g;
	s/[\t]/ /g;
	print `$_`;
}