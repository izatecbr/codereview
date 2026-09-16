package sis.pousada.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import sis.pousada.modelo.Cadastro;

import java.util.List;

public class CadastroJPA {

    private final EntityManagerFactory emf;

    public CadastroJPA() {
        this.emf = Persistence.createEntityManagerFactory("PU_SIS_POUSADA");
    }

    public Cadastro incluir(Cadastro cadastro) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(cadastro);
            em.getTransaction().commit();

            return cadastro;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Cadastro alterar(Cadastro cadastro) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Cadastro atualizado = em.merge(cadastro);
            em.getTransaction().commit();

            return atualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Cadastro> listar() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Cadastro c ORDER BY c.id",
                    Cadastro.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void fechar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
