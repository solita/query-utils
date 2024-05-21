package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.FunctionalA.sequence;
import static fi.solita.utils.query.QueryUtils.id;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.QueryUtils;

public class Predicates {
    
    private final ApplyZero<EntityManager> em;

    private final Configuration config;
    private final QueryUtils queryUtils;
    
    public Predicates(ApplyZero<EntityManager> em, Configuration config) {
        this.em = em;
        this.config = config;
        this.queryUtils = new QueryUtils(config);
    }
    
    protected CriteriaBuilder cb() {
        return em.get().getCriteriaBuilder();
    }
    
    @SuppressWarnings("unchecked")
    public <T> Expression<T> wrap(T value) {
        if (value instanceof Number) {
            for (String unwrappingFunctionName: config.wrapComparedNumbersWithFunction()) {
                return (Expression<T>) cb().function(unwrappingFunctionName, value.getClass(), cb().literal(value.toString())); 
            }
        }
        return cb().literal(value);
    }
    
    @SafeVarargs
    public final <E> Apply<CriteriaQuery<E>,Predicate> and(final Apply<CriteriaQuery<E>,Predicate>... predicates) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> t) {
                return cb().and(newArray(Predicate.class, sequence(t, predicates)));
            }
        };
    }
    
    @SafeVarargs
    public final <E> Apply<CriteriaQuery<E>,Predicate> or(final Apply<CriteriaQuery<E>,Predicate>... predicates) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> t) {
                return cb().or(newArray(Predicate.class, sequence(t, predicates)));
            }
        };
    }

    public <E, T> Apply<CriteriaQuery<E>,Predicate> isDefined(final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().isNotNull(selection.get(attribute));
            }
        };
    }
    
    public <E, T> Apply<CriteriaQuery<E>,Predicate> isNotDefined(final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().isNull(selection.get(attribute));
            }
        };
    }
    
    public <E, T> Apply<CriteriaQuery<E>,Predicate> equals(final SingularAttribute<? super E, T> attribute, final Option<T> value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                if (value.isDefined()) {
                    return cb().equal(selection.get(attribute), value.get());
                } else {
                    return cb().isNull(selection.get(attribute));
                }
            }
        };
    }
    
    public <E, T> Apply<CriteriaQuery<E>,Predicate> equalsOption(final SingularAttribute<? super E, Option<T>> attribute, final Option<T> value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                if (value.isDefined()) {
                    return cb().equal(selection.get(attribute), value.get());
                } else {
                    return cb().isNull(selection.get(attribute));
                }
            }
        };
    }
    
    public <E, T> Apply<CriteriaQuery<E>,Predicate> notEquals(final SingularAttribute<? super E, T> attribute, final Option<T> value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                if (value.isDefined()) {
                    return cb().notEqual(selection.get(attribute), value.get());
                } else {
                    return cb().isNotNull(selection.get(attribute));
                }
            }
        };
    }
    
    public <E, T> Apply<CriteriaQuery<E>,Predicate> notEqualsOption(final SingularAttribute<? super E, Option<T>> attribute, final Option<T> value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                if (value.isDefined()) {
                    return cb().notEqual(selection.get(attribute), value.get());
                } else {
                    return cb().isNotNull(selection.get(attribute));
                }
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> equalsIgnoreCase(final SingularAttribute<? super E, String> attribute, final Option<String> value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                if (value.isDefined()) {
                    return cb().equal(cb().lower(selection.get(attribute)), value.get().toLowerCase());
                } else {
                    return cb().isNull(selection.get(attribute));
                }
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> containsIgnoreCase(final SingularAttribute<? super E, String> attribute, final String value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                
                Expression<Integer> locateExpr = cb().locate(cb().lower(selection.get(attribute)), value.toLowerCase());
                return cb().not(cb().equal(locateExpr, 0));
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> startsWithIgnoreCase(final SingularAttribute<? super E, String> attribute, final String value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                
                Expression<Integer> locateExpr = cb().locate(cb().lower(selection.get(attribute)), value.toLowerCase());
                return cb().equal(locateExpr, 1);
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> startsWithIgnoreCaseOption(final SingularAttribute<? super E, Option<String>> attribute, final String value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                
                Expression<Integer> locateExpr = cb().locate(cb().lower(selection.get(attribute).as(String.class)), value.toLowerCase());
                return cb().and(cb().isNotNull(selection.get(attribute)), cb().equal(locateExpr, 1));
            }
        };
    }

    public <E, A> Apply<CriteriaQuery<E>,Predicate> in(final SingularAttribute<? super E, A> attribute, final Set<? super A> values) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                boolean enableInClauseOptimizations = !exists(QueryUtils.ImplementsProjectWithRegularInClause, newList(attribute.getJavaType(), attribute.getDeclaringType().getJavaType()));
                Path<A> path = selectionPath.get(attribute);
                return queryUtils.inExpr(path, values, em.get().getCriteriaBuilder(), enableInClauseOptimizations);
            }
        };
    }
    
    /**
     * Like {@link #in(SingularAttribute, Iterable, CriteriaQuery)}
     * but always explodes arguments to an ordinary in-clause. So doesn't use table/collection optimizations.
     */
    public <E, A> Apply<CriteriaQuery<E>,Predicate> in_regularForm(final SingularAttribute<? super E, A> attribute, final Set<? super A> values) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<A> path = resolveSelectionPath(query).get(attribute);
                return queryUtils.inExpr(path, values, em.get().getCriteriaBuilder(), false);
            }
        };
    }
    
    public <E, A> Apply<CriteriaQuery<E>,Predicate> notIn(final SingularAttribute<? super E, A> attribute, final Set<? super A> values) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                boolean enableInClauseOptimizations = !exists(QueryUtils.ImplementsProjectWithRegularInClause, newList(attribute.getJavaType(), attribute.getDeclaringType().getJavaType()));
                Path<A> path = selectionPath.get(attribute);
                return queryUtils.inExpr(path, values, em.get().getCriteriaBuilder(), enableInClauseOptimizations).not();
            }
        };
    }
    
    /**
     * Like {@link #notIn(SingularAttribute, Iterable, CriteriaQuery)}
     * but always explodes arguments to an ordinary in-clause. So doesn't use table/collection optimizations.
     */
    public <E, A> Apply<CriteriaQuery<E>,Predicate> notIn_regularForm(final SingularAttribute<? super E, A> attribute, final Set<? super A> values) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<A> path = resolveSelectionPath(query).get(attribute);
                return queryUtils.inExpr(path, values, em.get().getCriteriaBuilder(), false).not();
            }
        };
    }
    
    public <E, A> Apply<CriteriaQuery<E>,Predicate> inIds(final SingularAttribute<? super E, A> attribute, final Set<? extends Id<? super A>> values) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<A> path = resolveSelectionPath(query).get(attribute);
                return queryUtils.inExpr(path.get(id(path.getJavaType(), em.get())), values, em.get().getCriteriaBuilder());
            }
        };
    }

    public <E> Apply<CriteriaQuery<E>,Predicate> excluding(final Id<? super E> idToExclude) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.get()));
                return cb().notEqual(idPath, idToExclude);
            }
        };
    }

    public <E> Apply<CriteriaQuery<E>,Predicate> excluding(final Set<? extends Id<? super E>> idsToExclude) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                Path<Id<E>> idPath = selectionPath.get(QueryUtils.<E,Id<E>>id(selectionPath.getJavaType(), em.get()));
                return cb().not(queryUtils.inExpr(idPath, idsToExclude, em.get().getCriteriaBuilder()));
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> including(final Id<? super E> idToInclude) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.get()));
                return cb().equal(idPath, idToInclude);
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> including(final Set<? extends Id<? super E>> idsToInclude) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selectionPath = resolveSelectionPath(query);
                Path<Id<E>> idPath = selectionPath.get(QueryUtils.<E,Id<E>>id(selectionPath.getJavaType(), em.get()));
                return queryUtils.inExpr(idPath, idsToInclude, em.get().getCriteriaBuilder());
            }
        };
    }

    public <S,E extends S> Apply<CriteriaQuery<S>,Predicate> typeIs(final Class<E> type) {
        return new Apply<CriteriaQuery<S>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<S> query) {
                Path<S> path = resolveSelectionPath(query);
                return cb().equal(path.type(), type);
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> typeIsNot(final Class<? extends E> type) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> path = resolveSelectionPath(query);
                return cb().notEqual(path.type(), type);
            }
        };
    }

    public <E> Apply<CriteriaQuery<E>,Predicate> typeIn(final Set<Class<? extends E>> classes) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> path = resolveSelectionPath(query);
                return path.type().in(classes);
            }
        };
    }
    
    public <E> Apply<CriteriaQuery<E>,Predicate> typeNotIn(final Set<Class<? extends E>> classes) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> path = resolveSelectionPath(query);
                return path.type().in(classes).not();
            }
        };
    }

    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> lessThan(final SingularAttribute<? super E, T> attribute, final T value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().lessThan(selection.get(attribute), value);
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> lessThan(final T value, final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().lessThan(wrap(value), selection.get(attribute));
            }
        };
    };

    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> lessThanOrEqual(final SingularAttribute<? super E, T> attribute, final T value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().lessThanOrEqualTo(selection.get(attribute), wrap(value));
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> lessThanOrEqual(final T value, final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().lessThanOrEqualTo(wrap(value), selection.get(attribute));
            }
        };
    };

    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> greaterThan(final SingularAttribute<? super E, T> attribute, final T value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().greaterThan(selection.get(attribute), value);
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> greaterThan(final T value, final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().greaterThan(wrap(value), selection.get(attribute));
            }
        };
    };

    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> greaterThanOrEqual(final SingularAttribute<? super E, T> attribute, final T value) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().greaterThanOrEqualTo(selection.get(attribute), wrap(value));
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> greaterThanOrEqual(final T value, final SingularAttribute<? super E, T> attribute) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().greaterThanOrEqualTo(wrap(value), selection.get(attribute));
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> between(final T value, final SingularAttribute<? super E, T> a1, final SingularAttribute<? super E, T> a2) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().between(wrap(value), selection.get(a1), selection.get(a2));
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> between(final SingularAttribute<? super E, T> a, final T value1, final T value2) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().between(selection.get(a), wrap(value1), wrap(value2));
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> notBetween(final T value, final SingularAttribute<? super E, T> a1, final SingularAttribute<? super E, T> a2) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().between(wrap(value), selection.get(a1), selection.get(a2)).not();
            }
        };
    };
    
    public <E, T extends Comparable<? super T>> Apply<CriteriaQuery<E>,Predicate> notBetween(final SingularAttribute<? super E, T> a, final T value1, final T value2) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().between(selection.get(a), wrap(value1), wrap(value2)).not();
            }
        };
    };
    
    public <E> Apply<CriteriaQuery<E>,Predicate> like(final SingularAttribute<? super E, String> a, final String pattern) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().like(selection.get(a), pattern);
            }
        };
    };
    
    public <E> Apply<CriteriaQuery<E>,Predicate> notLike(final SingularAttribute<? super E, String> a, final String pattern) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().notLike(selection.get(a), pattern);
            }
        };
    };
    
    public <E> Apply<CriteriaQuery<E>,Predicate> likeIgnoreCase(final SingularAttribute<? super E, String> a, final String pattern) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().like(cb().lower(selection.get(a)), pattern.toLowerCase());
            }
        };
    };
    
    public <E> Apply<CriteriaQuery<E>,Predicate> notLikeIgnoreCase(final SingularAttribute<? super E, String> a, final String pattern) {
        return new Apply<CriteriaQuery<E>, Predicate>() {
            @Override
            public Predicate apply(CriteriaQuery<E> query) {
                Path<E> selection = resolveSelectionPath(query);
                return cb().like(cb().lower(selection.get(a)), pattern.toLowerCase()).not();
            }
        };
    };
}
