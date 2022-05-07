# VejbyGedcom
This repository contains a number of GEDCOM-related Java tools.

## GedcomFile
GedcomFile is a tool to create GEDCOM files for various online genealogical resources for Vejby, Holbo, Frederiksborg, Denmark, such as census files and church registries.
The tool is designed to be so generic that it can be used for local history elsewhere in Denmark.

The documentation folder contains .csv KIP files for Vejby from Dansk Demografisk Database.

The result of loading Vejby censuses into Gedcom can be followed at https://www.myerichsen.net/vejby/.

## DDDParser
DDDParser needs a kipdata.txt and a set of KIP csv files as input.
 
It uses a hardcoded list of villages within a parish to find all persons in
the csv files, who were born in one of these villages. You can update the
list in the initVillages() method.
 
The output is sent to a text file.
 
## DescendantCounter
DescendantCounter reads a GEDCOM file and finds the ancestors with most descendants.

## ParentFinder 
ParentFinder finds parents for each person born or christened in a given location.

Parameters:
<ul>
<li>Location name (e. g. village), where each character of [Ê¯Â∆ÿ≈] must be replaced with a "."</li>
<li>Full path to GEDCOM file</li>
<li>Path to an existing output directory</li>
</ul>
 
The program produces a .csv file with a row for each person found.
 
Parents are either extracted from the GEDCOM family record or from the citation source detail for the Christening event. When not using the family record, the first line of the citation detail must contain the location and the names of one or both parents.