package com.mtuci.poklad.models;

import lombok.Data;
import java.time.LocalDate;

/**
 * Модель для представления информации о билете лицензии.
 * Включает дату активации, окончания и дополнительные параметры, такие как подпись и описание.
 */
@Data
public class Ticket {

    /**
     * Текущая дата.
     */
    private LocalDate nowDate;

    /**
     * Дата активации лицензии.
     */
    private LocalDate activationDate;

    /**
     * Дата окончания действия лицензии.
     */
    private LocalDate expirationDate;

    /**
     * Срок действия лицензии в днях.
     */
    private Long expiration;

    /**
     * Идентификатор пользователя, для которого выдан билет.
     */
    private Long userID;

    /**
     * Идентификатор устройства, на которое выдан билет.
     */
    private Long deviceID;

    /**
     * Статус блокировки лицензии.
     */
    private boolean isBlockedLicense;

    /**
     * Цифровая подпись для обеспечения подлинности билета.
     */
    private String digitalSignature;

    /**
     * Описание лицензии.
     */
    private String description;
}
