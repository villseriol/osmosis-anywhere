import { drawTransform, renderVariant } from '@/coordinate-bounds-template'

renderVariant('generate-bounds-within', (figure) => {
    drawTransform(
        figure,
        { latitude: 34, longitude: 135 },
        { latitude: -32, longitude: 116 }
    )
})
