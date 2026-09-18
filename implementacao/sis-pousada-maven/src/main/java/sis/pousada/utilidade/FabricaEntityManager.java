package sis.pousada.utilidade;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class FabricaEntityManager {

    private static final String UNIDADE_PERSISTENCIA = "PU_SIS_POUSADA";

    private static EntityManagerFactory entityManagerFactory;

    private FabricaEntityManager() {
    }

    public static synchronized void inicializar() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA);
        }
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            inicializar();
        }
        return entityManagerFactory;
    }

    public static synchronized void finalizar() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
