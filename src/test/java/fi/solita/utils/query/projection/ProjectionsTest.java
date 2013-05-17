package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.*;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.projection.Project;

public class ProjectionsTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaProjectionQueries dao;

    @Autowired
    private JpaCriteriaQuery query;

    @Test
    public void get_projectSingle() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(query.single(dep.getId()), Project.id()));
    }

    @Test(expected = NoResultException.class)
    public void get_projectEmpty() {
        dao.get(query.all(Department.class), Project.id());
    }

    @Test(expected = NonUniqueResultException.class)
    public void get_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.get(query.all(Department.class), Project.id());
    }

    @Test
    public void find_projectSingle() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.find(query.single(dep.getId()), Project.id()).get());
    }

    @Test
    public void find_projectEmpty() {
        assertFalse(dao.find(query.all(Department.class), Project.id()).isDefined());
    }

    @Test(expected = NonUniqueResultException.class)
    public void find_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.find(query.all(Department.class), Project.id());
    }

    @Test
    public void findFirst_projectSingle() {
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_projectEmpty() {
        assertFalse(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_project_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.findFirst(query.all(Department.class), Project.id(), Order.by(Department_.name)).get());
        assertEquals(dep2.getId(), dao.findFirst(query.all(Department.class), Project.id(), Order.by(Department_.name).desc).get());
    }

    @Test
    public void getList_projection() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(dao.getList(query.all(Department.class), Project.id())));
    }

    @Test
    public void getList_pagingWithListAttribute_projection() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(newList(emp.getId()), dao.getList(query.related(dep, Department_.employees), Project.id(), Page.of(0, 2)));
    }

    @Test
    public void getList_projection_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId(), dep2.getId()), dao.getList(query.all(Department.class), Project.id(), Order.by(Department_.name)));
        assertEquals(newList(dep2.getId(), dep1.getId()), dao.getList(query.all(Department.class), Project.id(), Order.by(Department_.name).desc));
    }

    @Test
    public void getList_projection_paged_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId()), dao.getList(query.all(Department.class), Project.id(), Page.FIRST.withSize(1), Order.by(Department_.name)));
        assertEquals(newList(dep2.getId()), dao.getList(query.all(Department.class), Project.id(), Page.FIRST.withSize(1), Order.by(Department_.name).desc));
    }

    private CriteriaQuery<Department> allDepartmentsOrdered() {
        CriteriaQuery<Department> qOrdered = query.all(Department.class);
        return qOrdered.orderBy(em.getCriteriaBuilder().asc(qOrdered.getRoots().iterator().next().get("name")));
    }

    /*@Test
    @PaikkakontekstinaMaailma
    public void getList() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        CriteriaQuery<Liikennepaikka> q = query.all(Liikennepaikka.class);

        assertEquals(newSet(ids(lp1, lp2)), newSet(ids(dao.getList(q, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi)))));

        assertEquals(ids(lp1, lp2), ids(dao.getList(q, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Order.by(Liikennepaikka_.id))));
        assertEquals(ids(lp2, lp1), ids(dao.getList(q, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Order.by(Liikennepaikka_.id).desc)));

        assertEquals(ids(lp2),      ids(dao.getList(q, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Page.of(0, 1), Order.by(Liikennepaikka_.id).desc)));
        assertEquals(ids(lp1, lp2), ids(dao.getList(q, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Page.of(0, 2), Order.by(Liikennepaikka_.id))));

        assertEquals(5, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void getList_pagingWithOrderingDefinedInTheQuery() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        CriteriaQuery<Liikennepaikka> qOrdered = query.all(Liikennepaikka.class);
        qOrdered.orderBy(em.getCriteriaBuilder().asc(qOrdered.getRoots().iterator().next().get("id")));
        assertEquals(ids(lp1), ids(dao.getList(qOrdered, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Page.of(0, 1))));
        assertEquals(ids(lp2), ids(dao.getList(qOrdered, TestLiikennepaikkaDto_.c2(Liikennepaikka_.id, Liikennepaikka_.nimi), Page.of(1, 1))));

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void missingOptionalProducesNone() {
        cf.constructLiikennepaikka()._1.setMaakoodi(Option.<String> None())
                                       .setNimi(someText);
        ef.persistAll();
        long queryCount = getQueryCount();

        Pair<Option<String>, String> pair = dao.get(query.all(Liikennepaikka.class), Project.pair(Project.option(Liikennepaikka_.maakoodi), Liikennepaikka_.nimi));
        assertEquals(Pair.of(None(), someText), pair);

        assertEquals(1, getQueryCount() - queryCount);
    }

    @Test(expected = ProjectionSupport.OptionalAttributeNeedOptionTypeException.class)
    @Rollback
    public void queryingOptionalWithoutOptionalAttributeFails() {
        cf.constructLiikennepaikka()._1.setMaakoodi(Option.<String> None())
                                       .setNimi(someText);
        ef.persistAll();

        dao.get(query.all(Liikennepaikka.class), Project.pair(Liikennepaikka_.maakoodi, Liikennepaikka_.nimi));
    }

    @Test(expected = ProjectionSupport.RequiredAttributeMustNotHaveOptionTypeException.class)
    @Rollback
    public void queryingRequiredWithOptionalAttributeFails() {
        cf.constructLiikennepaikka()._1.setMaakoodi(Option.<String> None())
                                       .setNimi(someText);
        ef.persistAll();

        dao.get(query.all(Liikennepaikka.class), Project.pair(Project.option(Liikennepaikka_.maakoodi), Project.option(Liikennepaikka_.nimi)));
    }

    @Test(expected = ProjectionSupport.OptionalAttributeNeedOptionTypeException.class)
    @Rollback
    public void queryingOptionalRelationWithoutOptionalFails() {
        cf.constructLiikennepaikka();
        ef.persistAll();

        dao.get(query.all(LiikennepaikkaVersio.class), Project.pair(LiikennepaikkaVersio_.osiinjaettuLiikennepaikka, LiikennepaikkaVersio_.lyhenne));
    }

    @Test(expected = ProjectionSupport.RequiredAttributeMustNotHaveOptionTypeException.class)
    @Rollback
    public void queryingRequiredRelationWithOptionalAttributeFails() {
        cf.constructLiikennepaikka();
        ef.persistAll();

        dao.get(query.all(LiikennepaikkaVersio.class), Project.pair(Project.option(LiikennepaikkaVersio_.liikennepaikka), LiikennepaikkaVersio_.lyhenne));
    }

    @Test
    @PaikkakontekstinaMaailma
    public void projectRegularField() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1.setNimi(someText + 1);
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1.setNimi(someText + 2);
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> result = dao.getList(
                 query.all(Liikennepaikka.class),
             TestLiikennepaikkaDto_.c1(Liikennepaikka_.nimi));

        assertEquals(2, result.size());
        assertEquals(newSet(lp1.getNimi(), lp2.getNimi()), newSet(map(result, TestLiikennepaikkaDto_.nimi)));

        assertEquals(1, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    @AikakontekstinaIkuisuus
    public void getList_nPlus1() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        LiikennepaikanRaide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        LiikennepaikanRaide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        LiikennepaikanRaide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> result = dao.getList(
                query.all(Liikennepaikka.class),
            TestLiikennepaikkaDto_.c5(Liikennepaikka_.id, Liikennepaikka_.nimi, Liikennepaikka_.raiteet));

        assertEquals(newSet(ids(lp1, lp2)), newSet(ids(result)));
        for (TestLiikennepaikkaDto dto : result) {
            if (dto.getId().equals(lp1.getId())) {
                assertEquals(newSet(ids(r1, r3)), ids(dto.raiteet));
            } else {
                assertEquals(newSet(ids(r2)), ids(dto.raiteet));
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void getList_nPlus1_id() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        LiikennepaikanRaide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        LiikennepaikanRaide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        LiikennepaikanRaide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> result = dao.getList(
                query.all(Liikennepaikka.class),
            TestLiikennepaikkaDto_.c3(Liikennepaikka_.id, Liikennepaikka_.raiteet));

        assertEquals(ids(newSet(lp1, lp2)), ids(newSet(result)));
        for (TestLiikennepaikkaDto dto : result) {
            if (dto.getId().equals(lp1.getId())) {
                assertEquals(newSet(r1.getId(), r3.getId()), dto.raideIdt);
            } else {
                assertEquals(newSet(r2.getId()), dto.raideIdt);
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    @AikakontekstinaIkuisuus
    public void getList_nPlus1_dtoProjection() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        LiikennepaikanRaide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        LiikennepaikanRaide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        LiikennepaikanRaide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikanRaideDto> result = dao.getList(
                query.all(LiikennepaikanRaide.class),
            TestLiikennepaikanRaideDto_.c3(LiikennepaikanRaide_.id, Project.relation(LiikennepaikanRaide_.liikennepaikka,
                                           TestLiikennepaikkaDto_.c2(Liikennepaikka_.id,
                                                                     Liikennepaikka_.nimi))));

        assertEquals(newSet(ids(r1, r2, r3)), newSet(ids(result)));
        for (TestLiikennepaikanRaideDto dto : result) {
            if (dto.getId().equals(r2.getId())) {
                assertEquals(lp2.getId(), dto.liikennepaikka.getId());
            } else {
                assertEquals(lp1.getId(), dto.liikennepaikka.getId());
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void getList_nPlus1_dtoProjectionWithOnlyRegularField() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        cf.constructLiikennepaikanRaide(lp1);
        cf.constructLiikennepaikanRaide(lp2);
        cf.constructLiikennepaikanRaide(lp1);
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikanRaideDto> result = dao.getList(
                query.all(LiikennepaikanRaide.class),
            TestLiikennepaikanRaideDto_.c1(Project.relation(LiikennepaikanRaide_.liikennepaikka, TestLiikennepaikkaDto_.c1(Liikennepaikka_.nimi))));

        assertEquals(3, result.size());
        assertEquals(newSet(lp1.getNimi(), lp2.getNimi()), newSet(map(result, TestLiikennepaikanRaideDto_.liikennepaikka.andThen(TestLiikennepaikkaDto_.nimi))));

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void getList_nPlus1_dtoProjectionSet() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        LiikennepaikanRaide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        LiikennepaikanRaide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        LiikennepaikanRaide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> result = dao.getList(
                query.all(Liikennepaikka.class),
            TestLiikennepaikkaDto_.c6(Liikennepaikka_.id,
                                      Liikennepaikka_.aikapoikkeama,
                                      Project.option(Liikennepaikka_.maakoodi), Project.relation(Liikennepaikka_.raiteet,
                                      TestLiikennepaikanRaideDto_.c2(LiikennepaikanRaide_.id))));

        assertEquals(ids(lp1, lp2), ids(result));
        for (TestLiikennepaikkaDto dto : result) {
            if (dto.getId().equals(lp1.getId())) {
                assertEquals(newSet(ids(r1, r3)), ids(dto.raideDtot));
            } else {
                assertEquals(newSet(ids(r2)), ids(dto.raideDtot));
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @PaikkakontekstinaMaailma
    public void getList_nPlus1_attributeProjection() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1.setUicKoodi(someUicKoodi);
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1.setUicKoodi(someUicKoodi2);
        LiikennepaikanRaide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        LiikennepaikanRaide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        LiikennepaikanRaide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikanRaideDto> result = dao.getList(
                query.all(LiikennepaikanRaide.class),
            TestLiikennepaikanRaideDto_.c4(LiikennepaikanRaide_.id,
                                           Project.value(LiikennepaikanRaide_.liikennepaikka, Liikennepaikka_.uicKoodi)));

        assertEquals(newSet(ids(r1, r2, r3)), newSet(ids(result)));
        for (TestLiikennepaikanRaideDto dto : result) {
            if (dto.getId().equals(r2.getId())) {
                assertEquals(lp2.getUicKoodi(), dto.liikennepaikanUicKoodi);
            } else {
                assertEquals(lp1.getUicKoodi(), dto.liikennepaikanUicKoodi);
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }*/

   /* @Test
    @PaikkakontekstinaMaailma
    public void getList_nPlus1_attributeSetProjection() {
        Liikennepaikka lp1 = cf.constructLiikennepaikka()._1;
        Liikennepaikka lp2 = cf.constructLiikennepaikka()._1;
        Raide r1 = cf.constructLiikennepaikanRaide(lp1)._1;
        Raide r2 = cf.constructLiikennepaikanRaide(lp2)._1;
        Raide r3 = cf.constructLiikennepaikanRaide(lp1)._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> result = dao.getList(
                query.all(Liikennepaikka.class),
            TestLiikennepaikkaDto_.c4(Liikennepaikka_.id,
                                      Liikennepaikka_.aikapoikkeama,
                                      Project.value(Liikennepaikka_.raiteet)));

        assertEquals(ids(newSet(lp1, lp2)), ids(newSet(result)));
        for (TestLiikennepaikkaDto dto : result) {
            if (dto.getId().equals(lp1.getId())) {
                assertEquals(newSet(r1.getNimi(), r3.getNimi()), dto.raidenimet);
            } else {
                assertEquals(newSet(r2.getNimi()), dto.raidenimet);
            }
        }

        assertEquals(2, getQueryCount() - queryCount);
    }*/

    /*@SuppressWarnings("rawtypes")
    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_projectRelatedValue() {
        Tuple4<Liikennepaikka, LiikennepaikkaVersio, Liikennepaikkatyyppi, Liikennesuunnittelualue> lp = cf.constructLiikennepaikka();
        ef.persistAll();
        long queryCount = getQueryCount();

        LongId res = dao.get(
                query.single(lp._2.getId()),
            Project.value(Project.relation(LiikennepaikkaVersio_.liikennepaikka, Project.value(Liikennepaikka_.id))));

        assertEquals(lp._1.getId(), res);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_projectRelatedRelatedValue() {
        LiikennepaikanRaide raide = cf.constructLiikennepaikanRaide()._1;
        Tuple2<Puskin, PuskinVersio> e1 = cf.constructPuskin().take2();
        Tuple2<Puskin, PuskinVersio> e2 = cf.constructPuskin().take2();
        Kiskotus kiskotus = cf.constructKiskotus(e1, e2)._1;
        ef.newRaideKiskotus(raide, kiskotus);
        ef.persistAll();
        long queryCount = getQueryCount();

        Set<Id<IEntity>> res = dao.get(
                query.single(kiskotus.getId()),
            Project.value(Project.relation(Kiskotus_.raiteet, RaideKiskotus_.raide, Project.id())));

        assertEquals(newSet(raide.getId()), res);

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_projectRelatedRelatedValue_empty() {
        Kiskotus kiskotus = cf.constructKiskotus()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Set<Id<IEntity>> res = dao.get(
                query.single(kiskotus.getId()),
            Project.value(Project.relation(Kiskotus_.raiteet, RaideKiskotus_.raide, Project.id())));

        assertEmpty(res);

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    public void get_nPlus1_projectRelatedRelatedValue_halfEmpty() {
        Tuple2<Puskin, PuskinVersio> e1 = cf.constructPuskin().take2();
        set(e1._2, ElementtiVersio_.sijainti, ef.newKoordinaattisijainti(someAlue.getVasenAla()));

        Koordinaatti diff1 = Koordinaatti.lat(-100).lon(-100);
        Koordinaatti diff2 = Koordinaatti.lat(-200).lon(-200);

        Tuple2<Puskin, PuskinVersio> e2 = cf.constructPuskin().take2();
        set(e2._2, ElementtiVersio_.sijainti, ef.newKoordinaattisijainti(someAlue.getVasenAla().plus(diff1)));

        Tuple2<Puskin, PuskinVersio> e3 = cf.constructPuskin().take2();
        set(e3._2, ElementtiVersio_.sijainti, ef.newKoordinaattisijainti(someAlue.getVasenAla().plus(diff2)));

        Tuple2<Kiskotus,KiskotusVersio> kiskotus1 = cf.constructKiskotus(e1, e2);
        Tuple2<Kiskotus,KiskotusVersio> kiskotus2 = cf.constructKiskotus(e2, e3);

        Kytkentaryhma kytkentaryhma = ef.newKytkentaryhma();
        ef.newKiskoryhmaKiskotus(kytkentaryhma, kiskotus1._1);
        ef.newKiskoryhmaKiskotus(kytkentaryhma, kiskotus2._1);
        ef.persistAll();
        long queryCount = getQueryCount();

        doTest(kiskotus1._2, kytkentaryhma, someAlue);

        assertEquals(4, getQueryCount() - queryCount);
    }

    private void doTest(KiskotusVersio kiskotusVersio, Kytkentaryhma kytkentaryhma, @Paikkakonteksti Paikka alue) {
        Set<Set<KiskotusVersio>> res = dao.get(
                query.single(kytkentaryhma.getId()),
            Project.value(Project.relation(Kytkentaryhma_.kiskotukset, KiskoryhmaKiskotus_.kiskotus, Project.value(Kiskotus_.versiot))));

        assertEquals(1, size(flatten(res)));
        assertEquals(kiskotusVersio.getId(), head(flatten(res)).getId());
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_projectRelatedRelatedValue_dto() {
        LiikennepaikanRaide raide = cf.constructLiikennepaikanRaide()._1;
        Kiskotus kiskotus = cf.constructKiskotus()._1;
        ef.newRaideKiskotus(raide, kiskotus);
        ef.persistAll();
        long queryCount = getQueryCount();

        TestKiskotusDto dto = dao.get(
                query.single(kiskotus.getId()),
            TestKiskotusDto_.c1(Kiskotus_.id, Project.relation(Kiskotus_.raiteet, RaideKiskotus_.raide,
                                 TestLiikennepaikanRaideDto_.c2(LiikennepaikanRaide_.id))));
        assertEquals(kiskotus.getId(), dto.id);
        assertEquals(1, dto.raiteet.size());
        for (TestLiikennepaikanRaideDto d: dto.raiteet) {
            assertEquals(raide.getId(), d.id);
        }

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_projectRelatedRelatedValue_dto_empty() {
        Kiskotus kiskotus = cf.constructKiskotus()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        TestKiskotusDto dto = dao.get(
                query.single(kiskotus.getId()),
            TestKiskotusDto_.c1(Kiskotus_.id, Project.relation(Kiskotus_.raiteet, RaideKiskotus_.raide,
                                TestLiikennepaikanRaideDto_.c2(LiikennepaikanRaide_.id))));
        assertEquals(kiskotus.getId(), dto.id);
        assertEmpty(dto.raiteet);

        assertEquals(3, getQueryCount() - queryCount);
    }


    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_optionalRelation() {
        Tuple3<OsiinjaettuLiikennepaikka, Liikennepaikka, LiikennepaikkaVersio> ojlp = cf.constructOsiinjaettuLiikennepaikka().take3();
        ef.persistAll();
        long queryCount = getQueryCount();

        TestLiikennepaikkaVersioDto dto = dao.get(
                query.all(LiikennepaikkaVersio.class),
            TestLiikennepaikkaVersioDto_.c1(LiikennepaikkaVersio_.id,
                                            LiikennepaikkaVersio_.liikennepaikka,
                                            LiikennepaikkaVersio_.liikennepaikka,
                                            Project.option(LiikennepaikkaVersio_.osiinjaettuLiikennepaikka),
                                            Project.option(LiikennepaikkaVersio_.osiinjaettuLiikennepaikka)));

        assertEquals(ojlp._3.getId(), dto.versioId);
        assertEquals(ojlp._2.getId(), dto.liikennepaikkaId);
        assertEquals(ojlp._2.getId(), dto.liikennepaikka.getId());
        assertEquals(ojlp._1.getId(), dto.ojlpId.get());
        assertEquals(ojlp._1.getId(), dto.ojlp.get().getId());

        assertEquals(1, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_missingOptionalRelation() {
        Tuple2<Liikennepaikka, LiikennepaikkaVersio> lp = cf.constructLiikennepaikka().take2();
        ef.persistAll();
        long queryCount = getQueryCount();

        TestLiikennepaikkaVersioDto dto = dao.get(
                query.all(LiikennepaikkaVersio.class),
            TestLiikennepaikkaVersioDto_.c1(LiikennepaikkaVersio_.id,
                                            LiikennepaikkaVersio_.liikennepaikka,
                                            LiikennepaikkaVersio_.liikennepaikka,
                                            Project.option(LiikennepaikkaVersio_.osiinjaettuLiikennepaikka),
                                            Project.option(LiikennepaikkaVersio_.osiinjaettuLiikennepaikka)));

        assertEquals(lp._2.getId(), dto.versioId);
        assertEquals(lp._1.getId(), dto.liikennepaikkaId);
        assertEquals(lp._1.getId(), dto.liikennepaikka.getId());
        assertEquals(None(), dto.ojlpId);
        assertEquals(None(), dto.ojlp);

        assertEquals(1, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_missingPluralRelation() {
        Liikennepaikka lp = cf.constructLiikennepaikka()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        @SuppressWarnings("rawtypes")
        Pair<? extends Id, Set<Lahtolupa>> pair = dao.get(query.all(Liikennepaikka.class), Project.pair(Liikennepaikka_.id, Liikennepaikka_.lahtoluvat));

        assertEquals(lp.getId(), pair.left);
        assertEmpty(pair.right);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void get_nPlus1_missingPluralDtoRelation() {
        Liikennepaikka lp = cf.constructLiikennepaikka()._1;
        ef.persistAll();
        long queryCount = getQueryCount();

        Collection<TestLiikennepaikkaDto> ret = dao.getList(
                query.all(Liikennepaikka.class),
            TestLiikennepaikkaDto_.c6(Liikennepaikka_.id,
                                      Liikennepaikka_.aikapoikkeama,
                                      Project.option(Liikennepaikka_.maakoodi), Project.relation(Liikennepaikka_.raiteet,
                                      TestLiikennepaikanRaideDto_.c2(LiikennepaikanRaide_.id))));

        assertEquals(1, ret.size());
        assertEquals(lp.getId(), head(ret).id);
        assertEmpty(head(ret).raideDtot);

        assertEquals(2, getQueryCount() - queryCount);
    }


    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void embeddedTest() {
        PuskinVersio ev1 = cf.constructPuskin()._2;
        ef.persistAll();
        long queryCount = getQueryCount();

        PuskinDto dto = dao.get(query.single(ev1.getId()), PuskinDto_.c1(PuskinVersio_.sijainti));

        assertEquals(dto.getSijainti().getKoordinaatti(), ev1.getSijainti().getKoordinaatti());

        assertEquals(1, getQueryCount() - queryCount);
    }


    @Test
    @AikakontekstinaIkuisuus
    @PaikkakontekstinaMaailma
    public void hierarchical() {
        Tuple7<LiikennepaikanRaide, LiikennepaikanRaideVersio, Liikennepaikka, LiikennepaikkaVersio, Liikennepaikkatyyppi, Liikennesuunnittelualue, Ratamaksuluokka> lpr = cf.constructLiikennepaikanRaide();
        Tuple7<Kiskotus, KiskotusVersio, Puskin, PuskinVersio, Puskin, PuskinVersio, Railialue> k = cf.constructKiskotus();
        Rataosa rataosa = ef.newRataosa();
        RataosaVersio rataosaVersio = ef.newRataosaVersio(rataosa, lpr._3);
        RaideKiskotus raideKiskotus = ef.newRaideKiskotus(lpr._1, k._1);
        ef.persistAll();
        long queryCount = getQueryCount();

        // jos halutaan pitkän inner-joinin päästä tavaraa, kuten jonkin tietyn rataosan kaikki elementit:
        Collection<Id<KiskotusVersio>> rataosanKiskotusVersiot = dao.getList(
                query.related(rataosa, Rataosa_.versiot,
                              RataosaVersio_.liikennepaikat,
                              Liikennepaikka_.raiteet,
                              Raide_.kiskotukset,
                              RaideKiskotus_.kiskotus,
                              Kiskotus_.versiot),
                Project.<KiskotusVersio>id());
        assertEquals(newSet(k._2.getId()), newSet(rataosanKiskotusVersiot));

        assertEquals(1, getQueryCount() - queryCount);
        queryCount = getQueryCount();

        // tai sama toiseen suuntaan, polulla ei ole tietenkään väliä
        Collection<Id<Rataosa>> kiskotusVersionRataosat = dao.getList(
                query.related(k._2, KiskotusVersio_.kiskotus,
                                  Kiskotus_.raiteet,
                                  Query.<RaideKiskotus,LiikennepaikanRaide>cast(RaideKiskotus_.raide), // Javan vuoksi tässä Raide pitää castata LiikennepaikanRaiteeksi
                                  LiikennepaikanRaide_.liikennepaikka,
                                  Liikennepaikka_.rataosat,
                                  RataosaVersio_.rataosa),
                Project.<Rataosa>id());
        assertEquals(newList(rataosa.getId()), kiskotusVersionRataosat);

        assertEquals(1, getQueryCount() - queryCount);
        queryCount = getQueryCount();

        // jos halutaan dataa koko hierarkiasta, esim elementistä rataosaan kaikki id:t matkalla (huh...)
        @SuppressWarnings("rawtypes")
        Pair<? extends Id, ? extends Set<? extends Pair<? extends Id, ? extends Pair<? extends Id, ? extends Set<? extends Pair<? extends Id, ? extends Pair<? extends Id, ? extends Pair<? extends Id, ? extends Set<? extends Pair<? extends Id, ? extends Pair<? extends Id, ? extends Id>>>>>>>>>>> res =
                dao.get(query.single(k._3.getId()),
            Project.pair(Elementti_.id, Project.relation(Elementti_.kiskotukset1,
                    Project.pair(KiskotusVersio_.id, Project.relation(KiskotusVersio_.kiskotus,
                        Project.pair(Kiskotus_.id, Project.relation(Kiskotus_.raiteet,
                                Project.pair(RaideKiskotus_.id, Project.relation(RaideKiskotus_.raide,
                                        Project.pair(LiikennepaikanRaide_.id, Project.relation(LiikennepaikanRaide_.liikennepaikka,
                                                Project.pair(Liikennepaikka_.id, Project.relation(Liikennepaikka_.rataosat,
                                                        Project.pair(RataosaVersio_.id, Project.relation(RataosaVersio_.rataosa,
                                                                Project.pair(Rataosa_.id, Rataosa_.id))))))))))))))));

        assertEquals(res.left, k._3.getId());
        assertEquals(head(res.right).left, k._2.getId());
        assertEquals(head(res.right).right.left, k._1.getId());
        assertEquals(head(head(res.right).right.right).left, raideKiskotus.getId());
        assertEquals(head(head(res.right).right.right).right.left, lpr._1.getId());
        assertEquals(head(head(res.right).right.right).right.right.left, lpr._3.getId());
        assertEquals(head(head(head(res.right).right.right).right.right.right).left, rataosaVersio.getId());
        assertEquals(head(head(head(res.right).right.right).right.right.right).right, Pair.of(rataosa.getId(), rataosa.getId()));

        assertEquals(8, getQueryCount() - queryCount);
    }*/
}
