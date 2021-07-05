import {getLanguagesWeighted} from './utils';

describe('utils', () => {
    describe('getLanguages()', () => {
        describe('should format ordered languages with weights', () => {
            it('1 element', () => {
                navigator.mockedLanguages = ['fr'];
                expect(getLanguagesWeighted()).toBe('fr;q=1, *;q=0.9');
            });

            it('several elements', () => {
                navigator.mockedLanguages = ['fr-FR', 'fr', 'en-US', 'en'];
                expect(getLanguagesWeighted()).toBe('fr-FR;q=1, fr;q=0.9, en-US;q=0.8, en;q=0.7, *;q=0.6');
            });

            it('a lot of elements', () => {
                navigator.mockedLanguages = Array.from(Array(24), (_, i) => String.fromCharCode(i + 65));
                const languagesWeighted = getLanguagesWeighted();
                expect(languagesWeighted).toContain('A;q=1, B;q=0.99, C;q=0.98, D;q=0.97');
                expect(languagesWeighted).toContain('X;q=0.77, *;q=0.76');
            });
        });
    });
});
