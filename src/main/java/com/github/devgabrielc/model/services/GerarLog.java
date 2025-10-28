package com.github.devgabrielc.model.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerarLog {
    private static final Logger logger = LoggerFactory.getLogger(GerarLog.class);

    public void GeradorDeLogs() {
        logger.info("Este é um log em tempo real para o arquivo de texto.");
        logger.debug("Mensagem de debug.");
        logger.error("Ocorreu um erro!");
    }
}
