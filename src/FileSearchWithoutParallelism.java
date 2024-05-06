import java.io.*;

public class FileSearchWithoutParallelism {

    public static void main(String[] args) {
        File directory = new File("/home/gustavo/Área de Trabalho/parallel-and-distributed-computing/dataset_p"); // Define o caminho do diretório
        String searchTerm = "Laura"; // Define o termo de busca

        //dataset_g
        //Jon
        //Andrew
        //Kent

        //dataset_p
        //Jon
        //Michelle
        //Laura

        long startTime = System.currentTimeMillis(); // Inicia a medição do tempo

        int totalFound = searchFiles(directory, searchTerm); // Retorna o total de termos encontrados

        long endTime = System.currentTimeMillis(); // Encerra a medição do tempo
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Total de termos '" + searchTerm + "' encontrados: " + totalFound);
    }

    private static int searchFiles(File dir, String searchTerm) {
        int totalCount = 0;
        if (!dir.exists()) {
            System.out.println("O diretório não existe: " + dir.getAbsolutePath());
            return totalCount;
        }

        if (!dir.isDirectory()) {
            System.out.println("O caminho especificado não é um diretório: " + dir.getAbsolutePath());
            return totalCount;
        }

        File[] files = dir.listFiles((_, name) -> name.endsWith(".txt"));
        if (files == null) {
            System.out.println("Não foi possível ler os arquivos do diretório ou não há arquivos .txt: " + dir.getAbsolutePath());
            return totalCount;
        }

        for (File file : files) {
            totalCount += searchInFile(file, searchTerm);
        }
        return totalCount;
    }

    private static int searchInFile(File file, String searchTerm) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchTerm)) {
                    System.out.printf("Termo '%s' encontrado em %s na linha %d%n", searchTerm, file.getName(), lineNumber);
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + file.getName());
        }
        return count;
    }
}
