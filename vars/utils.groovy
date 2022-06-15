/**
*Convertion fichier txt indiquant les tests à passer en un tableau de string.
*La méthode va considérer comme une cellule du tableau chaque chaine de caractère du fichier séparée par un ';'.
*
*@param path Chemin du fichier texte à convertir.
*@return testSuite Tableau avec les chaines de caractères séparées par des ';'.
*/
String[] fileToArray (String path) {
    String names = readFile(path).replace("\n", "").replace("\r", "");
    String[] testSuite = names.split(";");

    return testSuite;
}

/**
*Supprime tous les fichiers dans le dossier passé en paramètre.
*
*@param path Chemin vers le dossier où l'on veut supprimer les fichiers.
*/
void cleanFilesInDir (String dirPath) {
    dir(dirPath) {
        def files = findFiles() 
        files.each { f -> 
            String supp = "rm "+f.toString();
            bat supp
        }
    }
}

//Récupère les .result dans le stash en les plaçant dans le repertoire en paramètre
/**
*Récupère les fichiers dans le stash de Jenkins et les archive dans le dossier passé en paramètre.
*
*@param pathToDest Chemin vers le dossier qui contiendra les fichiers récupés depuis le stash.
*@param stashes Liste des noms de stash à unstash
*/
void retreiveResultsAndArchive (String pathToDest, List<String> stashes) {
    dir(pathToDest) {
        for(String sn in stashes) 
            unstash sn
        archiveArtifacts artifacts: '**'
    }
}

/**
*Crée un dossier vide selon le chemin passé en paramètre.
*
*@param path Chemin du dossier à créer.
*/
void createDir (String path) {
    try{
        bat "if exist "+path+" rmdir /s /q "+path
        bat "mkdir "+path
        File dir = new File(path);
        if(!dir.isDirectory() && !dir.exists())
            throw new Exception();
    }
    catch (Exception e) {
        e.printStackTrace();
        println("Impossible de créer le dossier")
    }
}

/**
*Crée un fichier texte faisant office de rapport de test. 
*Ce rapport est construit en fonction de fichiers texte indiquant des résultats de tests.
*
*@param exeName Nom de l'exécutable qui se charge de construire le rapport en fonctions des fichiers récupérés.
*@param pathToExe Chemin vers le dossier où se trouve l'exécutable construisant le rapport.
*@param pathToReportDir Chemin vers le dossier où sera déposé le rapport généré par l'exécutable.
*@param pathToResultsDir Chemin vers le dossier contenant les résultats de test à récupérer pour construire le rapport.
*/
void buildReportAndArchive (String exeName, String pathToExe, String pathToReportDir, String pathToResultsDir) {
    StringBuilder cmd = new StringBuilder("cd "+pathToExe+"\n"+exeName+" "+pathToReportDir+" ");
     
    dir(pathToResultsDir) {
        def files = findFiles()
        files.each { f -> 
            cmd.append(pathToResultsDir+"/"+f.toString()+" ");
        }
    }
    
    bat cmd.toString();
    
    dir(pathToReportDir) {
        archiveArtifacts artifacts: '**'
    }
}

/**
*Supprime tous les fichiers et dossiers dans le répertoire courant.
*
*/
void cleanDirTotally () {
    def files = findFiles() 
    try {
        files.each { f -> 
            //Suppression dans le cas d'un fichier
            if(!f.isDirectory()) {
                String fName = f.toString();
                String supp = "rm " +fName.replace('/', '');
                bat supp
            }
            //Suppression dans le cas d'un dossier
            else if (f.isDirectory()) {
                String dName = f.toString();
                String suppD = "rmdir /s /q "+dName.replace('/', '');
                bat suppD
            }
        }
    }
    catch (Exception e) {
        e.printStackTrace();
        println("Erreur : un fichier ou un dossier n'a pas été supprimé");
    }
    
}