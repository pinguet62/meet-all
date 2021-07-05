declare global {
    interface Navigator {
        mockedLanguages?: string[]; // for mock
        userLanguage?: string;
        browserLanguage: string;
    }
}

export function getLanguagesWeighted() {
    const languages: string[] = [];
    if (window.navigator.mockedLanguages !== undefined) {
        languages.push(...navigator.mockedLanguages);
    } else if (navigator.languages !== undefined && navigator.languages.length > 0) {
        languages.push(...navigator.languages);
    } else {
        languages.push(navigator.userLanguage || navigator.language || navigator.browserLanguage);
    }

    languages.push('*');

    const count = languages.length;
    const delta = Math.pow(10, -Math.ceil(Math.log10(count)));
    return languages
        .map((language, index) => `${language};q=${1 - index * delta}`)
        .join(', ');
}
