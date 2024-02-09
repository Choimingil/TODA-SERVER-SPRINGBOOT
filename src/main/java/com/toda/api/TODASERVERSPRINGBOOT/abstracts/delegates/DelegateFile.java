package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseFile;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public final class DelegateFile implements BaseFile {
    private final ThreadPoolTaskExecutor taskExecutor;
    @Override
    public String readTxtFile(String filename) {
        ClassPathResource resource = new ClassPathResource(filename);
        try {
            return readFile(resource).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기로 파일 읽기
     * @param resource
     * @return
     */
    private Future<String> readFile(ClassPathResource resource){
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] fileData = FileCopyUtils.copyToByteArray(inputStream);
            return CompletableFuture.supplyAsync(()->new String(fileData, StandardCharsets.UTF_8),taskExecutor);
        }
        catch (IOException e){
            throw new WrongAccessException(WrongAccessException.of.READ_TXT_EXCEPTION);
        }
    }

    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }
}
