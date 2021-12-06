# PDFSplitAndSave
Split pdf into multiple pdf containing variable pages based on different key value

1. This program cuts a pdf file based on a specific key field (suppose Agent No) that is present in different pages.
2. Whenever the value of the key field changes the pdf is splitted into a new pdf is saved.
3. The new splitted pdf files can be saved directly into the harddisk or convert into bytecodes into database and read them again and
   convert into pdf files and save them in harddisk.
4. The server side coding was avoided because of limitations. But the portions are seperated so that anyone can save the bytecode into database
   with minimal tweak of codes.
5. The sample file is placed in the src/main/resources folder.
6. The output files get generated in src/main/output folder.
