import {
    drawArrow,
    drawBoxes,
    drawPoint,
    offsetBetween,
    renderVariant,
    shiftBox,
    squareAround,
} from '@/coordinate-bounds-template'

const FROM = { latitude: 34, longitude: 135 }
const TO = { latitude: 85, longitude: 70 }

renderVariant('generate-bounds-latitude-within', (figure) => {
    drawPoint(figure, FROM)
    drawBoxes(figure, shiftBox(squareAround(FROM), offsetBetween(FROM, TO)), {
        color: 'blue',
        shifted: true,
    })
    drawPoint(figure, TO, { labelSide: 'right' })
    drawArrow(figure, FROM, TO)
})
