//Convertion fichier txt indiquant les tests à passer en un tableau de string 
String[] fileToArray (String path) {
    String names = readFile(path).replace("\n", "").replace("\r", "");
    String[] testSuite = names.split(";");

    return testSuite;
}

void cleanDir (String path) {
    dir(path) {
        def files = findFiles() 
        files.each { f -> 
            String supp = "rm "+f.toString();
            bat supp
        }
    }
}