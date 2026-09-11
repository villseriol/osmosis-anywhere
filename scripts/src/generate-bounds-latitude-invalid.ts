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
const TO = { latitude: 100, longitude: 70 }

renderVariant('generate-bounds-latitude-invalid', (figure) => {
    drawBoxes(figure, shiftBox(squareAround(FROM), offsetBetween(FROM, TO)), {
        color: 'blue',
        shifted: true,
    })
    drawPoint(figure, FROM)
    drawPoint(figure, TO, { labelSide: 'right', marker: 'x' })
    drawArrow(figure, FROM, TO)
})
