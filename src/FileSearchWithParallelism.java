import java.io.*;
import java.util.concurrent.*;

public class FileSearchWithParallelism {

    public static void main(String[] args) {
        File directory = new File("/home/gustavo/Área de Trabalho/dataset_p"); // Defina o caminho do diretório
        String searchTerm = "Sandy"; // Defina o termo de busca
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            searchFiles(directory, searchTerm, executor);
        } finally {
            executor.shutdown();
        }
    }

    private static void searchFiles(File dir, String searchTerm, ExecutorService executor) {
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
            System.out.println("Não foi possível ler os arquivos do diretório: " + dir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            executor.execute(() -> {
                try {
                    searchInFile(file, searchTerm);
                } catch (Exception e) {
                    System.out.println("Erro ao processar o arquivo " + file.getName() + ": " + e.getMessage());
                }
            });
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
