import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSearchWithParallelism {

    public static void main(String[] args) {
        File directory = new File("/home/gustavo/Área de Trabalho/parallel-and-distributed-computing/dataset_p"); // Defina o caminho do diretório
        String searchTerm = "Jon"; // Defina o termo de busca
        //dataset_g
        //Jon
        //Andrew
        //Kent

        //dataset_p
        //Jon
        //Michelle
        //Laura
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger totalFound = new AtomicInteger(0); // Inicializa o contador atômico

        long startTime = System.currentTimeMillis(); // Inicia a medição do tempo

        try {
            searchFiles(directory, searchTerm, executor, totalFound);
        } finally {
            executor.shutdown();
            try {
                // Aguarda até que todas as tarefas sejam finalizadas ou até o tempo máximo de 1 dia
                if (!executor.awaitTermination(1, TimeUnit.DAYS)) {
                    executor.shutdownNow(); // Cancela as tarefas em execução
                }
            } catch (InterruptedException e) {
                executor.shutdownNow(); // Cancela as tarefas em execução se o await foi interrompido
                Thread.currentThread().interrupt(); // Restaura o status de interrupção
            }
        }

        long endTime = System.currentTimeMillis(); // Encerra a medição do tempo
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Total de termos '" + searchTerm + "' encontrados: " + totalFound.get());
    }

    private static void searchFiles(File dir, String searchTerm, ExecutorService executor, AtomicInteger totalFound) {
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
                    int foundCount = searchInFile(file, searchTerm);
                    totalFound.addAndGet(foundCount); // Adiciona a contagem encontrada ao total
                } catch (Exception e) {
                    System.out.println("Erro ao processar o arquivo " + file.getName() + ": " + e.getMessage());
                }
            });
        }
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
