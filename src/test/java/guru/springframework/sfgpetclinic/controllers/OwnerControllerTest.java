package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FROM = "owners/createOrUpdateOwnerForm";
    public static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @Mock
    Model model;

    @InjectMocks
    OwnerController controller;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp(){
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> owners = new ArrayList<>();
                    String name = invocation.getArgument(0);
                    if(name.equals("%Amador%")){
                        owners.add(new Owner(1l, "Paulina", "Amador"));
                        return owners;
                    } else if (name.equals("%DontFindMe%")) {
                        return owners;
                    } else if (name.equals("%FindMe%")) {
                        owners.add(new Owner(1L, "Alejandra", "Estrella"));
                        owners.add(new Owner(2L, "Liz", "Rose"));
                        return owners;
                    }
                    throw new RuntimeException("Invalid Argument");
                });

    }

    @Test
    void processFindFormWildCardStringAnnotation() {
        Owner owner = new Owner(1l, "Paulina", "Amador");
        String viewName = controller.processFindForm(owner, bindingResult, null);
        assertThat("%Amador%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
        verifyZeroInteractions(model);
    }

    @Test
    void processFindFormWildCardNotFound() {
        Owner owner = new Owner(1l, "Paulina", "DontFindMe");
        String viewName = controller.processFindForm(owner, bindingResult, null);
        verifyNoMoreInteractions(ownerService);
        assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
        verifyZeroInteractions(model);
    }

    @Test
    void processFindFormWildCardFound() {
        //given
        Owner owner = new Owner(1l, "Paulina", "FindMe");
        InOrder inOrder = inOrder(ownerService, model);

        //when
        String viewName = controller.processFindForm(owner, bindingResult, model);
        //then
        assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        //inorder asserts
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model, times(1)).addAttribute(anyString(),anyList());
        verifyNoMoreInteractions(model);
    }

    @Test
    void processCreationFormHasErrors() {
        //given
        Owner owner = new Owner(1l, "Pau", "Amador");
        given(bindingResult.hasErrors()).willReturn(true);
        //when
        String viewName = controller.processCreationForm(owner, bindingResult);
        //then
        assertThat(viewName).isEqualTo(OWNERS_CREATE_OR_UPDATE_OWNER_FROM);
    }



    @Test
    void processCreationFormNoErrors(){
        //given
        Owner owner = new Owner(5l, "Pau", "Amador");
        given(bindingResult.hasErrors()).willReturn(false);
        given(ownerService.save(any())).willReturn(owner);
        //when
        String viewName = controller.processCreationForm(owner, bindingResult);
        //then
        assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);

    }


}