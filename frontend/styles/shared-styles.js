// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
    html {
      --lumo-base-color: rgba(255,255,255,0);
      --lumo-primary-color: rgb(221,253,203);
      --lumo-primary-contrast-color: rgb(255,255,255);
      --lumo-primary-color-10pct: rgba(100,239,14,0.05);
      --lumo-primary-color-50pct: rgba(107,253,15,0.2);
      --lumo-primary-background-color: rgba(218,255,87,0.15);
      --lumo-font-family: Corbel, "Lucida Grande", "Lucida Sans Unicode", "Lucida Sans", "DejaVu Sans", "Bitstream Vera Sans", "Liberation Sans", Verdana, "Verdana Ref", sans-serif;
      --lumo-line-height-m: 1.4;
      --lumo-line-height-s: 1.2;
      --lumo-line-height-xs: 1.1;
      --lumo-border-radius: calc(var(--lumo-size-m) / 2);
      
      --lumo-success-color-10pct: hsl(112,100%,50%);
      --lumo-success-color-50pct: hsl(124,92%,23%);
      --lumo-success-color: hsl(128,100%,53%);
      --lumo-success-contrast-color: rgb(255,255,255);
      
      
      --lumo-header-text-color: hsl(214, 35%, 15%);
      --lumo-body-text-color: hsla(213,41%,16%,0.94);
      --lumo-secondary-text-color: hsla(214, 42%, 18%, 0.72);
      --lumo-tertiary-text-color: hsla(214, 45%, 20%, 0.5);
      --lumo-disabled-text-color: hsla(214, 50%, 22%, 0.26);
      --lumo-primary-text-color: hsl(230,100%,50%);
      --lumo-error-text-color: hsl(3, 92%, 53%);
      --lumo-success-text-color: hsl(117,100%,50%);
      --lumo-link-color: var(--lumo-primary-text-color);

    }
  </style>
</custom-style>


`;

document.head.appendChild($_documentContainer.content);
