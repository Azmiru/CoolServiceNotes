package org.example.coolservicenotes.Entity;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Jwt implements Serializable {
    private String token;

}
