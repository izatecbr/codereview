package sis.pousada.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import sis.pousada.modelo.hospedagem.HospedagamStatus;
import sis.pousada.modelo.hospedagem.Hospedagem;
import sis.pousada.utilidade.FabricaEntityManager;

import java.util.List;

public class HospedagemJPA {

    private final EntityManagerFactory emf;

    public HospedagemJPA() {
        this.emf = FabricaEntityManager.getEntityManagerFactory();
    }

    public Hospedagem incluir(Hospedagem hospedagem) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(hospedagem);
            em.getTransaction().commit();

            return hospedagem;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Hospedagem alterar(Hospedagem hospedagem) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Hospedagem atualizada = em.merge(hospedagem);
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

    public List<Hospedagem> listarPorHospede(String nomeHospede, List<HospedagamStatus> status) {
        EntityManager em = emf.createEntityManager();

        try {
            String filtro = "%" + (nomeHospede == null ? "" : nomeHospede.trim()) + "%";

            return em.createQuery(
                    "SELECT h FROM Hospedagem h "
                            + "WHERE LOWER(h.hospede.nome) LIKE LOWER(:nome) AND h.status IN :status "
                            + "ORDER BY h.duracao.dataInicial DESC, h.id DESC",
                    Hospedagem.class
            ).setParameter("nome", filtro).setParameter("status", status).getResultList();
        } finally {
            em.close();
        }
    }
}
