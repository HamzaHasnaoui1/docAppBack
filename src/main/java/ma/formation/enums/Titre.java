package ma.formation.enums;

public enum Titre {
   M, Mr, Mme, Mlle, Dr, Pr,MMe;

   public static Titre fromString(String value) {
      if (value == null || value.isEmpty()) {
         return null;
      }

      try {
         return Titre.valueOf(value);
      } catch (IllegalArgumentException e) {
         return Mr;
      }
   }
}

