package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void testDeleteByObject() {
        Speciality speciality = new Speciality();
        service.delete(speciality);
        verify(specialtyRepository).delete(any(Speciality.class));
    }

    @Test
    void findByIdTest() {
        Speciality speciality = new Speciality();
        when(specialtyRepository.findById(1l)).thenReturn(Optional.of(speciality));
        Speciality foundSpecialty = service.findById(1l);
        assertThat(foundSpecialty).isNotNull();
        verify(specialtyRepository).findById(anyLong());
    }

    @Test
    void deleteById() {
        service.deleteById(1l);
        service.deleteById(1l);
        verify(specialtyRepository, atLeastOnce()).deleteById(1l);
    }

    @Test
    void deleteByIdAtMost() {
        service.deleteById(1l);
        service.deleteById(1l);
        verify(specialtyRepository, atMost(5)).deleteById(1l);
    }

    @Test
    void deleteByIdNever() {
        service.deleteById(1l);
        service.deleteById(1l);
        verify(specialtyRepository,never()).deleteById(1l);
    }

    @Test
    void testDelete() {
        service.delete(new Speciality());
    }

    @Test
    void visitfindByIdBDD(){
        //given
        Speciality speciality = new Speciality();
        given(specialtyRepository.findById(1l)).willReturn(Optional.of(speciality));
        //when
        Speciality foundSpecialty = service.findById(1l);
        //then
        assertThat(foundSpecialty).isNotNull();
        then(specialtyRepository).should(times(1)).findById(anyLong());
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void testThrow() {
        doThrow(new RuntimeException("Boom")).when(specialtyRepository).delete(any());
        assertThrows(RuntimeException.class, () -> specialtyRepository.delete((new Speciality())));
        verify(specialtyRepository).delete(any());
    }

    @Test
    void testFindByIdThrows() {
        given(specialtyRepository.findById(1l)).willThrow(new RuntimeException("Boom"));
        assertThrows(RuntimeException.class, () -> service.findById(1l));
        then(specialtyRepository).should().findById(1l);
    }

    @Test
    void testDeleteBDD(){
        willThrow(new RuntimeException("Boom")).given(specialtyRepository).delete(any());
        assertThrows(RuntimeException.class, () -> specialtyRepository.delete(new Speciality()));
        then(specialtyRepository).should().delete(any());

    }

    @Test
    void testSaveLambda() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);
        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1l);
        given(specialtyRepository.save(argThat(arg -> arg.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);
        //when
        Speciality returnedSpecialty = service.save(speciality);
        //then
        assertThat(returnedSpecialty.getId()).isEqualTo(1l);
    }

}