package com.currency.gateway.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCommandDTO {
    @XmlAttribute
    private String id;

    @XmlElement(name = "get")
    private XmlCurrentDto get;

    @XmlElement(name = "history")
    private XmlHistoryDTO history;;
}
