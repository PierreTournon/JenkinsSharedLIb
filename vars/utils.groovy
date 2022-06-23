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
*Crée un fichier texte faisant office de rapport de test. 
*Ce rapport est construit en fonction de fichiers texte indiquant des résultats de tests.
*On part du principe que l'exécutable se trouve dans le repertoire courant
*
*@param exeName Nom de l'exécutable qui se charge de construire le rapport en fonctions des fichiers récupérés.
*@param pathToResultsDir Chemin vers le dossier contenant les résultats de test à récupérer pour construire le rapport.
*@param pathToReportDir Chemin vers le dossier où sera déposé le rapport généré par l'exécutable.
*/
void buildReportAndArchive (String exeName, String pathToResultsDir, String pathToReportDir) {
    StringBuilder cmd = new StringBuilder(exeName+" "+pathToResultsDir+" "+pathToReportDir);
     
    try {
        bat cmd.toString();
    }
    catch (Exception e) {
        e.printStackTrace();
        println("Erreur : impossible de lancer l'exécutable. Peut être que l'exécutable n'existe pas.")
    }
    
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

/**
*Récupère (clone) un dépôt distant Git dans le workspace courant de Jenkins.
*
*@param branchName Nom de la branche du dépôt que l'on va récupérer
*@param url Lien du dépôt Git
*@param credentials Crédits (token) pour autoriser la récupération dépôt.
*/
void retreiveDependencies (String branchName, String url, String credentials) {
    git branch: branchName, 
    url: url, 
    credentialsId: credentials
}


/**
*Stash les fichiers passés en entrée et renvoie une liste des noms de stash associés à chaque stash de fichier. 
*
*@param filesToStash Un suite de noms de fichiers présents relativement à l'appel de la méthode.
*@return artifactsNames Liste des noms associés à chacun des stash.
*/
ArrayList<String> stashArtifacts (String... filesToStash) {
    List<String> artifactsNames = new ArrayList<String>();
    for(int i = 0; i < filesToStash.size(); i++) {
        String stashName = "ArtifactStash"+i;
        stash includes: filesToStash[i], name: stashName
        artifactsNames.add(stashName);
    }
    
    return artifactsNames;
}

/**
*Récupère (unstash) les artéfacts.
*/
void retreiveArtifacts (List<String> artifacts) {
    for(str in artifacts) {
        unstash str
    }
}