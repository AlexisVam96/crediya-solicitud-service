package co.com.crediya.r2dbc.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReactiveAdapterOperationsTest {

    static class DummyEntity {
        String id;
        String value;
        // getters/setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    static class DummyData {
        String id;
        String value;
        // getters/setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    interface DummyRepository extends ReactiveCrudRepository<DummyData, String>, ReactiveQueryByExampleExecutor<DummyData> {}

    static class DummyAdapter extends ReactiveAdapterOperations<DummyEntity, DummyData, String, DummyRepository> {
        DummyAdapter(DummyRepository repo, ObjectMapper mapper, Function<DummyData, DummyEntity> fn) {
            super(repo, mapper, fn);
        }
    }

    DummyRepository repository;
    ObjectMapper mapper;
    DummyAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(DummyRepository.class);
        mapper = mock(ObjectMapper.class);
        Function<DummyData, DummyEntity> toEntityFn = data -> {
            DummyEntity entity = new DummyEntity();
            entity.setId(data.getId());
            entity.setValue(data.getValue());
            return entity;
        };
        adapter = new DummyAdapter(repository, mapper, toEntityFn);
    }

    @Test
    void save_shouldMapAndSaveEntity() {
        DummyEntity entity = new DummyEntity();
        entity.setId("1");
        entity.setValue("test");
        DummyData data = new DummyData();
        data.setId("1");
        data.setValue("test");

        when(mapper.map(entity, DummyData.class)).thenReturn(data);
        when(repository.save(data)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.save(entity))
                .expectNextMatches(e -> e.getId().equals("1") && e.getValue().equals("test"))
                .verifyComplete();

        verify(repository).save(data);
    }

    @Test
    void findById_shouldReturnMappedEntity() {
        DummyData data = new DummyData();
        data.setId("2");
        data.setValue("foo");
        when(repository.findById("2")).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.findById("2"))
                .expectNextMatches(e -> e.getId().equals("2") && e.getValue().equals("foo"))
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnMappedEntities() {
        DummyData data1 = new DummyData();
        data1.setId("1");
        data1.setValue("a");
        DummyData data2 = new DummyData();
        data2.setId("2");
        data2.setValue("b");
        when(repository.findAll()).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByExample_shouldReturnMappedEntities() {
        DummyEntity probe = new DummyEntity();
        probe.setId("3");
        probe.setValue("bar");
        DummyData data = new DummyData();
        data.setId("3");
        data.setValue("bar");

        when(mapper.map(probe, DummyData.class)).thenReturn(data);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(data));

        StepVerifier.create(adapter.findByExample(probe))
                .expectNextMatches(e -> e.getId().equals("3") && e.getValue().equals("bar"))
                .verifyComplete();
    }

    @Test
    void saveAllEntities_shouldMapAndSaveEntities() {
        DummyEntity entity1 = new DummyEntity();
        entity1.setId("1");
        entity1.setValue("a");
        DummyEntity entity2 = new DummyEntity();
        entity2.setId("2");
        entity2.setValue("b");

        DummyData data1 = new DummyData();
        data1.setId("1");
        data1.setValue("a");
        DummyData data2 = new DummyData();
        data2.setId("2");
        data2.setValue("b");

        when(mapper.map(entity1, DummyData.class)).thenReturn(data1);
        when(mapper.map(entity2, DummyData.class)).thenReturn(data2);
        when(repository.saveAll(any(Flux.class))).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.saveAllEntities(Flux.just(entity1, entity2)))
                .expectNextMatches(e -> e.getId().equals("1") && e.getValue().equals("a"))
                .expectNextMatches(e -> e.getId().equals("2") && e.getValue().equals("b"))
                .verifyComplete();

        verify(repository).saveAll(any(Flux.class));
    }

    @Test
    void saveData_shouldSaveDataAndReturnMono() {
        DummyData data = new DummyData();
        data.setId("10");
        data.setValue("test-data");

        when(repository.save(data)).thenReturn(Mono.just(data));

        StepVerifier.create(adapter.saveData(data))
                .expectNextMatches(d -> d.getId().equals("10") && d.getValue().equals("test-data"))
                .verifyComplete();

        verify(repository).save(data);
    }
}