package os.expert.integration.microstream;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;
import jakarta.nosql.Entity;
import org.eclipse.jnosql.mapping.Embeddable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * Scanner classes that will load entities with both Entity and Embeddable
 * annotations and repositories: interfaces that extend DataRepository
 * and has the Repository annotation.
 */
public enum ClassScanner {

    INSTANCE;

    private final Set<Class<?>> entities;
    private final Set<Class<?>> repositores;


    ClassScanner() {
        entities = new HashSet<>();
        repositores = new HashSet<>();

        Logger logger = Logger.getLogger(ClassScanner.class.getName());
        logger.fine("Starting scan class to find entities, embeddable and repositories.");
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            this.entities.addAll(result.getClassesWithAnnotation(Entity.class).loadClasses());
            this.repositores.addAll(result.getClassesWithAnnotation(Repository.class)
                    .getInterfaces().loadClasses(DataRepository.class));
        }
        logger.fine(String.format("Finished the class scan with entities %d, and repositories: %d"
                , entities.size(), repositores.size()));

    }


    /**
     * Returns the classes that that has the {@link Entity} annotation
     *
     * @return classes with {@link Entity} annotation
     */
    public Set<Class<?>> entities() {
        return unmodifiableSet(entities);
    }

    /**
     * Returns repositories: interfaces that extend DataRepository and has the Repository annotation.
     *
     * @return the repositories items
     */
    public Set<Class<?>> repositories() {
        return unmodifiableSet(repositores);
    }

    /**
     * Returns repositories {@link Class#isAssignableFrom(Class)} the parameter
     *
     * @param filter the repository filter
     * @return the list
     */
    public Set<Class<?>> repositories(Class<? extends DataRepository> filter) {
        Objects.requireNonNull(filter, "filter is required");
        return repositores.stream().filter(filter::isAssignableFrom)
                .filter(c -> Arrays.asList(c.getInterfaces()).contains(filter))
                .collect(toUnmodifiableSet());
    }
}