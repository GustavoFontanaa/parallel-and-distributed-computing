import java.io.*;

public class FileSearchWithoutParallelism {

    public static void main(String[] args) {
        File directory = new File("/home/gustavo/Área de Trabalho/dataset_g"); // Define o caminho do diretório
        String searchTerm = "Sandy"; // Define o termo de busca

        long startTime = System.currentTimeMillis(); // Inicia a medição do tempo

        searchFiles(directory, searchTerm);

        long endTime = System.currentTimeMillis(); // Encerra a medição do tempo
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
    }

    private static void searchFiles(File dir, String searchTerm) {
        if (!dir.exists()) {
            System.out.println("O diretório não existe: " + dir.getAbsolutePath());
            return;
        }

        if (!dir.isDirectory()) {
            System.out.println("O caminho especificado não é um diretório: " + dir.getAbsolutePath());
            return;
        }

        File[] files = dir.listFiles((_, name) -> name.endsWith(".txt"));
        if (files == null) {
            System.out.println("Não foi possível ler os arquivos do diretório ou não há arquivos .txt: " + dir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            searchInFile(file, searchTerm);
        }
    }

    private static void searchInFile(File file, String searchTerm) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchTerm)) {
                    System.out.printf("Termo '%s' encontrado em %s na linha %d%n", searchTerm, file.getName(), lineNumber);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + file.getName());
        }
    }
}
