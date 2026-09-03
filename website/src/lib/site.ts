export type Lang = 'fr' | 'en';
export type Page = 'home' | 'safety' | 'pilot';

export const repo = 'https://github.com/charifmahmoudi/mobile-ot-security-assessment';
export const wiki = `${repo}/wiki`;

export function sitePath(base: string, lang: Lang, page: Page = 'home'): string {
  const prefix = lang === 'fr' ? base : `${base}en/`;
  if (page === 'home') return prefix;
  return `${prefix}${page}/`;
}

export function evaluationIssueUrl(lang: Lang): string {
  const template = lang === 'fr' ? 'atlas-evaluation-fr.md' : 'atlas-evaluation-en.md';
  const title = lang === 'fr' ? '[Évaluation Atlas] ' : '[Atlas evaluation] ';
  return `${repo}/issues/new?template=${encodeURIComponent(template)}&title=${encodeURIComponent(title)}`;
}
