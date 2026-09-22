package sis.pousada.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import sis.pousada.modelo.acomodacao.Acomodacao;
import sis.pousada.utilidade.FabricaEntityManager;

import java.util.List;

public class AcomodacaoJPA {

    private final EntityManagerFactory emf;

    public AcomodacaoJPA() {
        this.emf = FabricaEntityManager.getEntityManagerFactory();
    }

    public Acomodacao incluir(Acomodacao acomodacao) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(acomodacao);
            em.getTransaction().commit();

            return acomodacao;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Acomodacao alterar(Acomodacao acomodacao) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Acomodacao atualizada = em.merge(acomodacao);
            em.getTransaction().commit();

            return atualizada;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Acomodacao> listarPorLegenda(String legenda) {
        EntityManager em = emf.createEntityManager();

        try {
            String filtro = "%" + (legenda == null ? "" : legenda.trim()) + "%";

            return em.createQuery(
                    "SELECT a FROM Acomodacao a WHERE LOWER(a.legenda) LIKE LOWER(:legenda) ORDER BY a.legenda",
                    Acomodacao.class
            ).setParameter("legenda", filtro).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Acomodacao> listar() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Acomodacao a ORDER BY a.id",
                    Acomodacao.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
